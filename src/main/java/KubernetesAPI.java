import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.nio.file.Paths;

public class KubernetesAPI {
    private final String link;

    public KubernetesAPI(String platform_ip, String port) {
        this.link = Paths.get("http:/", platform_ip, ":", port).toString();
    }

    public String getLink() {
        return link;
    }

    public long getPodCapacity() throws IOException, ApiException {
        Long storage = 0l;

        try {
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);

            CoreV1Api api = new CoreV1Api();
            V1NodeList list = api.listNode(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            for (V1Node item : list.getItems()) {
                String quantity = item.getStatus().getAllocatable().get("ephemeral-storage").toSuffixedString();
                storage += Long.parseLong(quantity);
            }
        } catch (ApiException | IOException e) {
            System.err.println(e.getMessage());
        }

        return storage;
    }
    public static void main(String[] args) throws IOException, ApiException {
        KubernetesAPI new_kubes = new KubernetesAPI("localhost", "8080");
        System.out.println(new_kubes.getPodCapacity());
    }
}
