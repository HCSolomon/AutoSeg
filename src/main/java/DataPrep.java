import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.kubernetes.client.ApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataPrep {
    private final KubernetesAPI kubernetesAPI;
    private final S3API s3API;

    public DataPrep(KubernetesAPI kubernetesAPI, String bucket_name) {
        this.kubernetesAPI = kubernetesAPI;
        this.s3API = new S3API(bucket_name);
    }

    public S3API getS3API() {
        return s3API;
    }

    public KubernetesAPI getKubernetesAPI() {
        return kubernetesAPI;
    }

    public List<List<S3ObjectSummary>> groupImages() throws IOException, ApiException {
        AmazonS3 s3 = s3API.getS3();
        List<S3ObjectSummary> images = s3API.getImages();

        long capacity = kubernetesAPI.getPodCapacity() / 2;
        long size = 0;

        List<List<S3ObjectSummary>> groups = new ArrayList<>();
        List<S3ObjectSummary> group = new ArrayList<>();
        try {
            for (S3ObjectSummary os : images) {
                size += os.getSize();
                group.add(os);
                if (size > capacity) {
                    groups.add(new ArrayList<S3ObjectSummary>(group));
                    group.clear();
                }
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        return groups;
    }
}
