package steps.t1.cdn;

import core.enums.Role;
import core.helper.http.Http;

import static core.helper.Configure.cdnProxy;

public abstract class AbstractCdnClient {

    protected static final String apiUrl = "/api/v1/projects/{}/";

    protected static Http getRequestSpec() {
        return new Http(cdnProxy)
                .setRole(Role.CLOUD_ADMIN);
    }
}
