package steps.secretService;

import core.enums.Role;
import core.helper.http.Http;
import models.cloud.secretService.EnginePage;
import models.cloud.secretService.Secret;
import models.cloud.secretService.SecretResponse;

import static api.routes.SecretServiceAdminAPI.*;
import static models.Entity.serialize;

public class SecretServiceAdminSteps {

    public static SecretResponse postV1Secrets(Secret secret){
        return Http.builder().setRole(Role.SUPERADMIN).body(serialize(secret)).api(postV1Secrets).extractAs(SecretResponse.class);
    }

    public static void deleteV1SecretsId(String secretId){
        Http.builder().setRole(Role.SUPERADMIN).api(deleteV1SecretsId, secretId);
    }

    public static EnginePage getV1Engines(){
        return Http.builder().setRole(Role.SUPERADMIN).api(getV1Engines).extractAllPages(EnginePage.class);
    }
}
