package tests.routes.cdn;

import core.helper.http.Path;
import tests.routes.Api;

import static core.helper.Configure.cdnProxy;

public class CdnResourceApi implements Api {

    @Route(method = Api.Method.GET, path = "/v1/projects/{project_name}/resources", status = 200)
    public static Path getResources;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/resources/{id}", status = 204)
    public static Path deleteResourceById;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/resources/{id}/enable_streaming", status = 200)
    public static Path enableLiveStreaming;

    @Override
    public String url() {
        return cdnProxy + "/api";
    }
}
