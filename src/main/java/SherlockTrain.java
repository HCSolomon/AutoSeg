import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.kubernetes.client.ApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class SherlockTrain {
    private DataPrep dataPrep;
    private List<List<S3ObjectSummary>> objects;
    private String model_pref;
    private String model_name;

    public SherlockTrain(String platform_ip, String port, String bucket_name, String model_pref, String model_name) throws IOException, ApiException {
        KubernetesAPI new_kube = new KubernetesAPI(platform_ip, port);
        this.dataPrep = new DataPrep(new_kube, bucket_name);
        this.objects = dataPrep.groupImages();
        this.model_pref = model_pref;
        this.model_name = model_name;
    }

    public void microTrain() {
        try {
            URL url = new URL(dataPrep.getKubernetesAPI().getLink());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("bucket_name", this.dataPrep.getS3API().getData());
            connection.setRequestProperty("bucket_prefix", this.model_pref);
            connection.setRequestProperty("model_name", this.model_name);

            String json = "";
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                json = stringBuilder.toString();
            }
            else {
                System.out.println("Error: Response code " + connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void train() {
        for (List<S3ObjectSummary> images : this.objects) {
            for (S3ObjectSummary os : images) {
                String path = os.getKey();
                String name = path.substring(path.lastIndexOf('/') + 1);
                String folder = path.substring(0, path.lastIndexOf('/'));
                String new_path = Paths.get(folder, "train", name).toString();
                AmazonS3 s3 = dataPrep.getS3API().getS3();
                s3.copyObject(dataPrep.getS3API().getData(), path,
                        dataPrep.getS3API().getData(), new_path);
            }
        }
        System.out.println("** Completed training model. **");
    }
}
