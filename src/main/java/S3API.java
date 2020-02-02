import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.kubernetes.client.ApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class S3API {
    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
    public final String data;
    private long size;

    public S3API(String bucket_name) {
        this.data = bucket_name;
    }

    public List<S3ObjectSummary> getImages() {
        ListObjectsV2Result objects = s3.listObjectsV2(this.data);
        List<S3ObjectSummary> images = objects.getObjectSummaries();
        return images;
    }

    public AmazonS3 getS3() {
        return this.s3;
    }

    public List<List<S3ObjectSummary>> groupImages() throws IOException, ApiException {
        ListObjectsV2Result objects = s3.listObjectsV2(this.data);
        List<S3ObjectSummary> images = objects.getObjectSummaries();
        KubernetesAPI kubernetesAPI = new KubernetesAPI();

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

    public static void main(String[] args) throws IOException, ApiException {
        S3API s = new S3API("data-watson");

        List<S3ObjectSummary> ims = s.getImages();
        for (S3ObjectSummary os : ims) {
            System.out.println(os.getKey());
        }

        s.groupImages();
    }
}
