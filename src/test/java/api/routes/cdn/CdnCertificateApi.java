package api.routes.cdn;

import api.routes.Api;
import core.helper.http.Path;

import static core.helper.Configure.cdnProxy;

public class CdnCertificateApi implements Api {

    @Route(method = Api.Method.GET, path = "/v1/projects/{project_name}/certificates", status = 200)
    public static Path getCertificates;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/certificates/{id}", status = 204)
    public static Path deleteCertificateById;

    @Override
    public String url() {
        return cdnProxy + "/api";
    }
}
