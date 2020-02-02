import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class KubernetesAPI {
    public int getPodCount() throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listNamespacedPod(
                "watson-eks",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        int count = 0;
        for (V1Pod item : list.getItems()) {
            count++;
        }
        return count;
    }
    public static void main(String[] args) throws IOException, ApiException {
        KubernetesAPI newkubs = new KubernetesAPI();
        System.out.println(newkubs.getPodCount());
    }
}
