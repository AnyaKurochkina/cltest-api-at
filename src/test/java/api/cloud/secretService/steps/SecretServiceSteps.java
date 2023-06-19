package api.cloud.secretService.steps;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import org.json.JSONObject;

import static api.routes.SecretServiceAPI.getV1Health;
import static api.routes.SecretServiceAPI.getV1Secrets;

public class SecretServiceSteps {

    public static boolean getV1Health(){
        return Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1Health).jsonPath().getBoolean("ok");
    }

    public static JSONObject getV1Secrets(QueryBuilder queryBuilder){
        return new JSONObject(Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1Secrets, queryBuilder).toString());
    }
}
