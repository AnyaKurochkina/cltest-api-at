package steps.secretService;

import core.enums.Role;
import core.helper.http.Http;
import models.cloud.secretService.Secret;
import models.cloud.secretService.SecretResponse;

import static api.routes.SecretServiceAPI.getV1Health;
import static api.routes.SecretServiceAdminAPI.deleteV1SecretsId;
import static api.routes.SecretServiceAdminAPI.postV1Secrets;
import static models.Entity.serialize;

public class SecretServiceSteps {

    public static boolean getV1Health(){
        return Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1Health).jsonPath().getBoolean("ok");
    }
}
