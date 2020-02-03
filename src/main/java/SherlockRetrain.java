import io.kubernetes.client.ApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SherlockRetrain extends SherlockBase {
    public SherlockRetrain(String bucket, String model_pref, String model_name) throws IOException, ApiException {
        super(bucket, model_pref, model_name);
    }

    public void retrain(String url, String bucket) {
        try {
            URL client = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) client.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("epochs", "5");
            connection.setRequestProperty("batch_size", "30");
            connection.setRequestProperty("bucket_name", bucket);
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
}
