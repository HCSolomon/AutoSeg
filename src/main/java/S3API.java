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
    private final AmazonS3 s3;
    private final String data;
    private long size;

    public S3API(AmazonS3 s3, String bucket_name) {
        this.s3 = s3;
        this.data = bucket_name;
    }

    public String getData() {
        return this.data;
    }

    public List<S3ObjectSummary> getImages() {
        ListObjectsV2Result objects = s3.listObjectsV2(this.data);
        List<S3ObjectSummary> images = objects.getObjectSummaries();
        return images;
    }

    public AmazonS3 getS3() {
        return this.s3;
    }

    public static void main(String[] args) throws IOException, ApiException {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        S3API s = new S3API(s3, "data-watson");

        List<S3ObjectSummary> ims = s.getImages();
        for (S3ObjectSummary os : ims) {
            System.out.println(os.getKey());
        }
    }
}
