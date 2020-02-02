import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;

public class S3API {
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
    public List<S3ObjectSummary> getImages(String bucket) {
        ListObjectsV2Result objects = s3.listObjectsV2(bucket);
        List<S3ObjectSummary> images = objects.getObjectSummaries();
        return images;
    }

    public static void main(String[] args) {
        S3API s = new S3API();

        List<S3ObjectSummary> ims = s.getImages("data-watson");
        for (S3ObjectSummary os : ims) {
            System.out.println(os.getKey());
        }
    }
}
