import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class DataPrepProducer {
    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
    List<Bucket> buckets;
    private String KAFKA_IP;
    private String KAFKA_PORT;

    public DataPrepProducer(String kafka_ip, String kafka_port) {
        buckets = s3.listBuckets();
        this.KAFKA_IP = kafka_ip;
        this.KAFKA_PORT = kafka_port;
    }

    public void publishBuckets() {
        Properties props = new Properties();
        props.put("bootstrap.servers", Paths.get(KAFKA_IP, ":", KAFKA_PORT).toString());
        props.put("transactional.id", "t-id");
        Producer<String, String> producer = new KafkaProducer<String, String>(props, new StringSerializer(), new StringSerializer());

        producer.initTransactions();

        try {
            producer.beginTransaction();
            for (Bucket b : buckets) {
                producer.send(new ProducerRecord<String, String>("image_buckets", b.getName()));
            }
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            producer.close();
        } catch (KafkaException e) {
            producer.abortTransaction();
        }
    }
}
