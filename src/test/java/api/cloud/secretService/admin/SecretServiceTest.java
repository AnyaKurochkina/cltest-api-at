package api.cloud.secretService.admin;

import api.cloud.secretService.AbstractSecretTest;
import core.helper.http.QueryBuilder;
import io.qameta.allure.TmsLink;
import models.cloud.secretService.SecretResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.secretService.SecretServiceAdminSteps;
import steps.secretService.SecretServiceSteps;

import java.util.Arrays;


public class SecretServiceTest extends AbstractSecretTest {
    public static String mask = "*****";

    @Test
    @TmsLink("")
    @DisplayName("Проверка /health")
    void getV1Health() {
        Assertions.assertTrue(SecretServiceSteps.getV1Health());
    }

    @Test
    @TmsLink("")
    @DisplayName("Создание/Удаление секрета")
    void postV1Secrets() {
        SecretResponse secretResponse = generateSecret();
        secretResponse.delete();
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение списка секретов")
    void getV1Secrets() {
        final JSONObject data = new JSONObject().put("key", "value");
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        JSONObject response = SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("segment", secretResponse.getEngine().getSegment())
                .add("engine_name", secretResponse.getEngine().getName())
                .add("uri", secretResponse.getUri()));
        Assertions.assertTrue(data.similar(response));
    }

    @Test
    @TmsLink("")
    @DisplayName("Добавление/удаление данных для секрета")
    void deleteV1SecretsSecretIdData() {
        final JSONObject data = new JSONObject().put("k1", "v1").put("k2", "v2").put("k3", "v3");
        SecretResponse secretResponse = generateSecret();
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        SecretServiceAdminSteps.deleteV1SecretsSecretIdData(secretResponse.getId(), Arrays.stream(data.keySet().toArray()).filter(e -> e.equals("k1")).toArray());
        data.remove("k1");
        JSONObject response = SecretServiceSteps.getV1Secrets(new QueryBuilder()
                .add("uri", secretResponse.getUri()));
        Assertions.assertTrue(data.similar(response));
    }

    @Test
    @TmsLink("")
    @DisplayName("Получение данных для секрета")
    void getV1SecretsSecretIdData() {
        SecretResponse secretResponse = generateSecret();
        final JSONObject data = new JSONObject().put("k1", "v1");
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        JSONObject response = SecretServiceAdminSteps.getV1SecretsSecretIdData(secretResponse.getId());
        Assertions.assertEquals(response.get("k1"), mask);
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление данных для секрета")
    void patchV1SecretsSecretIdData() {
        SecretResponse secretResponse = generateSecret();
        final JSONObject data = new JSONObject().put("k1", "v1");
        final JSONObject upData = new JSONObject().put("k1", "v2");
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        SecretServiceAdminSteps.patchV1SecretsSecretIdData(secretResponse.getId(), upData);
        JSONObject response = SecretServiceAdminSteps.getV1SecretsSecretIdData(secretResponse.getId());
        Assertions.assertEquals(response.get("k1"), mask);
    }
}
