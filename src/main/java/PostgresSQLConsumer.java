import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class PostgresSQLConsumer {
    private String KAFKA_IP;
    private String KAFKA_PORT;
    private String PSQL_IP;
    private String PSQL_PORT;
    private Connection c;

    public PostgresSQLConsumer(String KAFKA_IP, String KAFKA_PORT, String PSQL_IP, String PSQL_PORT, Connection c) {
        this.KAFKA_IP = KAFKA_IP;
        this.KAFKA_PORT = KAFKA_PORT;
        this.PSQL_IP = PSQL_IP;
        this.PSQL_PORT = PSQL_PORT;
        this.c = c;
    }

    public void consumeInference() throws SQLException {
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
                JsonObject json_results = msg.get("data");
                String model = msg.get("model").toString();
                List<JsonObject> results = new Gson().fromJson(json_results, new TypeToken<List<JsonObject>>() {}.getType());
                for (JsonObject result : results) {
                    HashMap<String, JsonObject> result_metadata = new Gson().fromJson(result, new TypeToken<HashMap<String, JsonObject>>() {}.getType());
                    String name = result_metadata.get("name").toString();
                    List<JsonObject> predictions = new Gson().fromJson(result_metadata.get("prediction"), new TypeToken<HashMap<String, JsonObject>>() {}.getType());
                    HashMap<String, String> prediction = new Gson().fromJson(predictions.get(0), new TypeToken<HashMap<String, String>>() {}.getType());
                    String label = prediction.get("label");
                    float prob = Integer.parseInt(prediction.get("probability"));
                    Statement stmt = null;
                    c.createStatement();
                    String sql = "INSERT INTO model_stats (MODELNAME, NAME, LABEL, PROB) "
                            + "VALUES(" + model + ", " + name + ", " + label + ", " + prob + ");";
                    stmt.executeUpdate(sql);
                }
            }
        }
    }
}
