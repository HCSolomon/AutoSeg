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

public class SherlockTrain extends SherlockBase {
    public SherlockTrain(String platform_ip, String port, String bucket_name, String model_pref, String model_name) throws IOException, ApiException {
        super(platform_ip, port, bucket_name, model_pref, model_name);
    }

    public void microTrain() {
        try {
            URL url = new URL(getDataPrep().getKubernetesAPI().getLink());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("bucket_name", getDataPrep().getS3API().getData());
            connection.setRequestProperty("bucket_prefix", getModelPrefix());
            connection.setRequestProperty("model_name", getModelName());

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
        for (List<S3ObjectSummary> images : getObjects()) {
            for (S3ObjectSummary os : images) {
                String path = os.getKey();
                String name = path.substring(path.lastIndexOf('/') + 1);
                String folder = path.substring(0, path.lastIndexOf('/'));
                String new_path = Paths.get(folder, "train", name).toString();
                AmazonS3 s3 = getDataPrep().getS3API().getS3();
                s3.copyObject(getDataPrep().getS3API().getData(), path,
                        getDataPrep().getS3API().getData(), new_path);
            }
        }
        System.out.println("** Completed training model. **");
    }
}
