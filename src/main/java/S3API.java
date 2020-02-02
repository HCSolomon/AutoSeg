import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.nio.file.Paths;
import java.util.List;

public class S3API {
    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
    private final String data;

    public S3API(String bucket_name) {
        this.data = bucket_name;
    }

    public List<S3ObjectSummary> getImages() {
        ListObjectsV2Result objects = s3.listObjectsV2(this.data);
        List<S3ObjectSummary> images = objects.getObjectSummaries();
        return images;
    }

    public void prepImages() {
        ListObjectsV2Result objects = s3.listObjectsV2(this.data);
        List<S3ObjectSummary> images = objects.getObjectSummaries();

        try {
            for (S3ObjectSummary os : images) {
                String path = os.getKey();
                String name = path.substring(path.lastIndexOf('/') + 1);
                String folder = path.substring(0,path.lastIndexOf('/'));
                String new_path = Paths.get(folder, "train", name).toString();
                s3.copyObject(this.data, path, this.data, new_path);
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        S3API s = new S3API("data-watson");

        List<S3ObjectSummary> ims = s.getImages();
        for (S3ObjectSummary os : ims) {
            System.out.println(os.getKey());
        }

        s.prepImages();
        List<S3ObjectSummary> ims2 = s.getImages();

        for (S3ObjectSummary os : ims2) {
            System.out.println(os.getKey());
        }
    }
}
