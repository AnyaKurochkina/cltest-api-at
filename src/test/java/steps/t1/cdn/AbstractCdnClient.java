package steps.t1.cdn;

import core.enums.Role;
import core.helper.http.Http;

public abstract class AbstractCdnClient {

    protected static Http getRequestSpec() {
        return Http.builder()
                .setRole(Role.CLOUD_ADMIN);
    }
}
