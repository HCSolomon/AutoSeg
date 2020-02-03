import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import io.kubernetes.client.ApiException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataPrep {
    private final KubernetesAPI kubernetesAPI;
    private final S3API s3API;
    private String KAFKA_IP;
    private String KAFKA_PORT;

    public DataPrep(KubernetesAPI kubernetesAPI, AmazonS3 s3, String bucket_name, String KAFKA_IP, String KAFKA_PORT) {
        this.kubernetesAPI = kubernetesAPI;
        this.s3API = new S3API(s3, bucket_name);
        this.KAFKA_IP = KAFKA_IP;
        this.KAFKA_PORT = KAFKA_PORT;
    }

    public S3API getS3API() {
        return s3API;
    }

    public KubernetesAPI getKubernetesAPI() {
        return kubernetesAPI;
    }

    public void dataProducer(String task) throws IOException, ApiException {
        AmazonS3 s3 = s3API.getS3();
        List<S3ObjectSummary> images = s3API.getImages();
        Properties props = new Properties();
        props.put("bootstrap.servers", Paths.get(KAFKA_IP, ":", KAFKA_PORT).toString());
        props.put("transactional.id", "t-id");
        Producer<String, String> producer = new KafkaProducer<String, String>(props, new StringSerializer(), new StringSerializer());

        long capacity = kubernetesAPI.getPodCapacity() / 2;
        long size = 0;

        List<S3ObjectSummary> group = new ArrayList<>();
        try {
            for (S3ObjectSummary os : images) {
                size += os.getSize();
                group.add(os);
                if (size > capacity) {
                    String json = new Gson().toJson(group);

                    producer.initTransactions();

                    producer.send(new ProducerRecord<String, String>(task, json));
                    group.clear();
                }
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}
