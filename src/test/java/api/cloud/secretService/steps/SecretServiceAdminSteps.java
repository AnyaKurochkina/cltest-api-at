package api.cloud.secretService.steps;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import api.cloud.secretService.models.EnginePage;
import api.cloud.secretService.models.Secret;
import api.cloud.secretService.models.SecretResponse;
import org.json.JSONObject;

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

    public static void postV1SecretsSecretIdData(String secretId, JSONObject data){
        Http.builder().setRole(Role.SUPERADMIN).body(new JSONObject().put("data", data)).api(postV1SecretsSecretIdData, secretId);
    }

    public static void deleteV1SecretsSecretIdData(String secretId, Object[] data){
        Http.builder().setRole(Role.SUPERADMIN).api(deleteV1SecretsSecretIdData, secretId, new QueryBuilder().add("data[]", data));
    }

    public static JSONObject getV1SecretsSecretIdData(String secretId){
        return new JSONObject(Http.builder().setRole(Role.SUPERADMIN).api(getV1SecretsSecretIdData, secretId).toString());
    }

    public static void patchV1SecretsSecretIdData(String secretId, JSONObject data){
        Http.builder().setRole(Role.SUPERADMIN).body(new JSONObject().put("data", data)).api(patchV1SecretsSecretIdData, secretId);
    }
}
