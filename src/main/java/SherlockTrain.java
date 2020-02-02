import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.nio.file.Paths;
import java.util.List;

public class SherlockTrain {
    private S3API s3API;
    public void train(List<S3ObjectSummary> images) {

        for (S3ObjectSummary os : images) {
            String path = os.getKey();
            String name = path.substring(path.lastIndexOf('/') + 1);
            String folder = path.substring(0, path.lastIndexOf('/'));
            String new_path = Paths.get(folder, "train", name).toString();
            s3API.getS3().copyObject(s3API.data, path, s3API.data, new_path);
        }
    }
}
