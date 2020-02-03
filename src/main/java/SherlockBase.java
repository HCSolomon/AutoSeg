import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.kubernetes.client.ApiException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class SherlockBase {
    private String bucket_name;
    private String model_pref;
    private String model_name;

    public SherlockBase(String bucket_name, String model_pref, String model_name) throws IOException, ApiException {
        this.bucket_name = bucket_name;
        this.model_pref = model_pref;
        this.model_name = model_name;
    }

    public String getModelName() { return model_name; }

    public String getModelPrefix() { return model_pref; }
}
