package api.routes.cdn;

import api.routes.Api;
import core.helper.http.Path;

import static core.helper.Configure.cdnProxy;

public class CdnResourceApi implements Api {

    @Route(method = Api.Method.GET, path = "/v1/projects/{project_name}/resources", status = 200)
    public static Path getResources;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/resources/{id}", status = 204)
    public static Path deleteResourceById;

    @Override
    public String url() {
        return cdnProxy + "/api";
    }
}
