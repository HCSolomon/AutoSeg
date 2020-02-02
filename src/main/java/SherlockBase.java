import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.kubernetes.client.ApiException;

import java.io.IOException;
import java.util.List;

public class SherlockBase {
    private DataPrep dataPrep;
    private List<List<S3ObjectSummary>> objects;
    private String model_pref;
    private String model_name;

    public SherlockBase(String platform_ip, String port, String bucket_name, String model_pref, String model_name) throws IOException, ApiException {
        KubernetesAPI new_kube = new KubernetesAPI(platform_ip, port);
        this.dataPrep = new DataPrep(new_kube, bucket_name);
        this.objects = dataPrep.groupImages();
        this.model_pref = model_pref;
        this.model_name = model_name;
    }

    public DataPrep getDataPrep() {
        return dataPrep;
    }

    public List<List<S3ObjectSummary>> getObjects() {
        return objects;
    }

    public String getModelPrefix() {
        return model_pref;
    }

    public String getModelName() {
        return model_name;
    }
}
