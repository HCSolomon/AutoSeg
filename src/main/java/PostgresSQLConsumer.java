import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class PostgresSQLConsumer {
    private String KAFKA_IP;
    private String KAFKA_PORT;
    private String PSQL_IP;
    private String PSQL_PORT;

    public PostgresSQLConsumer(String KAFKA_IP, String KAFKA_PORT, String PSQL_IP, String PSQL_PORT) {
        this.KAFKA_IP = KAFKA_IP;
        this.KAFKA_PORT = KAFKA_PORT;
        this.PSQL_IP = PSQL_IP;
        this.PSQL_PORT = PSQL_PORT;
    }

    public void consumeInference() {
        Properties props = new Properties();
        props.put("bootstrap.servers", Paths.get(KAFKA_IP, ":", KAFKA_PORT));
        props.put("group.id", "inference_consumer");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("postgres_inference"));
        while (true) {
            final ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                HashMap<String, JsonObject> msg = new Gson().fromJson(record.value(), new TypeToken<HashMap<String, JsonObject>>() {}.getType());
                JsonObject json_results = msg.get("results");
                List<JsonObject> results = new Gson().fromJson(json_results, new TypeToken<List<JsonObject>>() {}.getType());
                for (JsonObject result : results) {
                    HashMap<String, JsonObject> result_metadata = new Gson().fromJson(result, new TypeToken<HashMap<String, JsonObject>>() {}.getType());
                    HashMap<String, JsonObject> prediction = new Gson().fromJson(result_metadata.get("prediction"), new TypeToken<HashMap<String, JsonObject>>() {}.getType());
                    List<JsonObject> conf = new Gson().fromJson(prediction.get("proability"), new TypeToken<List<JsonObject>>() {}.getType());
                }
            }
        }
    }
}
