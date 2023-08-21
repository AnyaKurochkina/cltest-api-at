package api.cloud.secretService;

import api.cloud.secretService.models.Secret;
import api.cloud.secretService.models.SecretResponse;
import api.cloud.secretService.steps.SecretServiceAdminSteps;
import api.cloud.secretService.steps.SecretServiceSteps;
import core.enums.Role;
import core.helper.http.QueryBuilder;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Feature("Сервис секретов")
@Epic("Действия с секретом (admin api)")
public class SecretServiceAdminTest extends AbstractSecretTest {
    public static String mask = "*****";

    @Test
    @TmsLink("1714119")
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
    @TmsLink("1714120")
    @DisplayName("Получение данных для секрета (admin)")
    void getV1SecretsSecretIdData() {
        SecretResponse secretResponse = generateSecret();
        final JSONObject data = new JSONObject().put("k1", "v1");
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        JSONObject response = SecretServiceAdminSteps.getV1SecretsSecretIdData(secretResponse.getId());
        Assertions.assertEquals(response.get("k1"), mask);
    }

    @Test
    @TmsLink("1714121")
    @DisplayName("Обновление данных для секрета")
    void patchV1SecretsSecretIdDataUpdate() {
        SecretResponse secretResponse = generateSecret();
        final JSONObject data = new JSONObject().put("k1", "v1");
        final JSONObject upData = new JSONObject().put("k1", "v2");
        SecretServiceAdminSteps.postV1SecretsSecretIdData(secretResponse.getId(), data);
        AssertUtils.assertContains(SecretServiceAdminSteps.patchV1SecretsSecretIdData(secretResponse.getId(), upData),
                "\"Ключи, которые не были найдены: \"", "\"Успешно обновленные ключи: k1\"");
        JSONObject response = SecretServiceSteps.getV1Secrets(new QueryBuilder().add("uri", secretResponse.getUri()));
        Assertions.assertEquals(upData.get("k1"), response.get("k1"));
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление данных для секрета (ключ отсутствует)")
    void patchV1SecretsSecretIdDataUpdateFail() {
        SecretResponse secretResponse = generateSecret();
        final JSONObject data = new JSONObject().put("k1", "v1");
        AssertUtils.assertContains(SecretServiceAdminSteps.patchV1SecretsSecretIdData(secretResponse.getId(), data),
                "\"Ключи, которые не были найдены: k1\"", "\"Успешно обновленные ключи: \"");
    }

    @Test
    @TmsLink("")
    @DisplayName("Обновление тегов секрета")
    void patchV1SecretsId() {
        SecretResponse secretResponse = generateSecret();
        Secret updatedSecret = new Secret(secretResponse);
        updatedSecret.setTags(Arrays.asList("tag1", "tag2"));
        SecretResponse updatedSecretResponse = SecretServiceAdminSteps.patchV1SecretsId(secretResponse.getId(), updatedSecret);
        Assertions.assertEquals(updatedSecret, new Secret(updatedSecretResponse));
    }

    @Test
    @TmsLink("")
    @DisplayName("Просмотр секрета (admin api)")
    void getV1SecretsId() {
        SecretResponse secretResponse = generateSecret();
        SecretResponse getSecretResponse = SecretServiceAdminSteps.getV1SecretsId(secretResponse.getId(), Role.SUPERADMIN);
        getSecretResponse.setReadOnlyAccess(null);
        Assertions.assertEquals(secretResponse, getSecretResponse);
    }
}
