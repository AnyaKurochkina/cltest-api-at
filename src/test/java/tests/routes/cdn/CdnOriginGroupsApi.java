package tests.routes.cdn;

import core.helper.http.Path;
import tests.routes.Api;

import static core.helper.Configure.cdnProxy;

public class CdnOriginGroupsApi implements Api {

    @Route(method = Api.Method.GET, path = "/v1/projects/{project_name}/origin-groups", status = 200)
    public static Path getSourceGroups;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/origin-groups/{id}", status = 204)
    public static Path deleteSourceGroupById;

    @Override
    public String url() {
        return cdnProxy + "/api";
    }
}
