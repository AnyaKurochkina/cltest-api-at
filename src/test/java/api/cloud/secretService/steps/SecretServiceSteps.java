package api.cloud.secretService.steps;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Step;
import org.json.JSONObject;

import java.util.Objects;

import static tests.routes.SecretServiceAPI.*;

public class SecretServiceSteps {

    public static boolean getV1Health(){
        return Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1Health).jsonPath().getBoolean("ok");
    }

    public static String getV1Version(){
        return Objects.requireNonNull(Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1Version).jsonPath().getString("version"), "Поле version пустое");
    }

    @Step("Получение данных секрета")
    public static JSONObject getV1Secrets(QueryBuilder queryBuilder){
        return new JSONObject(Http.builder().setRole(Role.CLOUD_ADMIN).api(getV1Secrets, queryBuilder).toString());
    }
}
