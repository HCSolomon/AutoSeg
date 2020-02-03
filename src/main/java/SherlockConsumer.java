import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.ApiException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class SherlockConsumer {
    private AmazonS3 s3;
    private String KAFKA_IP;
    private String KAFKA_PORT;

    public SherlockConsumer(AmazonS3 s3, String KAFKA_IP, String KAFKA_PORT) {
        this.s3 = s3;
        this.KAFKA_IP = KAFKA_IP;
        this.KAFKA_PORT = KAFKA_PORT;
    }

    public void consumer(String task,
                         String bucket,
                         String path,
                         String model_pref,
                         String model_name,
                         String url) throws IOException, ApiException {
        Properties props = new Properties();
        props.put("bootstrap.servers", Paths.get(KAFKA_IP, ":", KAFKA_PORT));
        props.put("group.id", "image_consumer");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(task));
        while (true) {
            final ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                List<S3ObjectSummary> ims = new Gson().fromJson(record.value(), new TypeToken<List<S3ObjectSummary>>() {}.getType());
                for (S3ObjectSummary os : ims) {
                    process(bucket, path, task);
                }
                if (task == "train") {
                    SherlockTrain train = new SherlockTrain(bucket, model_pref, model_name);
                    train.train(url, bucket);
                }
                else if (task == "retrain") {
                    SherlockRetrain retrain = new SherlockRetrain(bucket, model_pref, model_name);
                    retrain.retrain(url, bucket);
                }
                else {
                    SherlockInference infer = new SherlockInference(bucket, model_pref, model_name);
                    infer.inference(url, bucket);
                }
            }
        }
    }

    public void process(String bucket, String path, String task) {
        String name = path.substring(path.lastIndexOf('/') + 1);
        String folder = path.substring(0, path.lastIndexOf('/'));
        String new_path = Paths.get(folder, task, name).toString();
        s3.copyObject(bucket, path, bucket, new_path);
    }

    public void clear(String bucket, String path, String task) {
        ListObjectsV2Result objects = s3.listObjectsV2(bucket);
        List<S3ObjectSummary> remove = objects.getObjectSummaries();
        for (S3ObjectSummary os : remove) {
            if (path.substring(path.lastIndexOf('/') - 1) == "train") {
                s3.deleteObject(bucket, os.getKey());
            }
        }
    }
}
