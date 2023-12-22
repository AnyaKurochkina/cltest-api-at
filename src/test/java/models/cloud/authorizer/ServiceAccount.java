package models.cloud.authorizer;

import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.keyCloak.KeyCloakClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;
import java.util.List;

import static core.utils.Waiting.sleep;

@Builder
@Getter
@Log4j2
public class ServiceAccount extends Entity implements KeyCloakClient {
    String projectId;
    String secret;
    @Setter
    String id;
    String title;
    String jsonTemplate;
    String accessId;
    Role role;
    @Setter
    Boolean withApiKey;

    @Singular
    public List<String> roles;

    @Override
    public Entity init() {
        if (withApiKey == null) {
            withApiKey = true;
        }
        jsonTemplate = "/authorizer/service_accounts.json";
        if (title == null)
            title = new Generex("[a-z]{5,18}").random();
        if (role == null)
            role = Role.VIEWER;
        if (projectId == null)
            projectId = ((Project) Project.builder().build().createObject()).getId();
        return this;
    }

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.service_account.title", title)
                .set("$.service_account.policy.bindings.[0].role", role.toString())
                .build();
    }

    @Step("Создание статического ключа досутпа hcp bucket")
    public void createStaticKey() {
        accessId = new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(new JSONObject("{\"access_key\":{\"description\":\"Ключ\",\"password\":\"JP1mD3rlh67Hek@zb%ClSCFUxvUj4q6Z0ZfjfnK3VQhXt5xMLplE$B7237FPHu\"}}"))
                .post("/v1/projects/{}/service_accounts/{}/access_keys", projectId, id)
                .assertStatus(201)
                .jsonPath()
                .getString("data.access_id");

        sleep(10000);
        JsonPath jsonPathStatus = new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}/service_accounts/{}/access_keys", projectId, id)
                .assertStatus(200)
                .jsonPath();

        Assertions.assertEquals(Collections.singletonList("active"), jsonPathStatus.get("data.status"),
                "Статический ключ не создался, текущий статус: " + jsonPathStatus.get("data.status"));
    }

    @Step("Удаление статического ключа доступа hcp bucket")
    public void deleteStaticKey() {
        new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .delete("/v1/projects/{}/service_accounts/{}/access_keys/{}", projectId, id, id)
                .assertStatus(204);

        String keyStatus = "";
        int counter = 6;
        JsonPath jsonPath = null;
        log.info("Проверка статуса статического ключа");
        while (!keyStatus.equals("[active]") && counter > 0) {
            sleep(10000);
            jsonPath = new Http(Configure.iamURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/v1/projects/{}/service_accounts/{}/access_keys", projectId, id)
                    .assertStatus(200)
                    .jsonPath();

            log.info("Статус статического ключа: " + jsonPath.get("data.status").toString());
            keyStatus = jsonPath.get("data.status").toString();
            counter = counter - 1;
        }
        log.info("Итоговый статус статического ключа " + keyStatus);

        Assertions.assertTrue(jsonPath.getList("data").isEmpty(),
                "При удалении статического ключа ожидается в ответе пустой блок data, но data:\n" + jsonPath.getList("data").toString());
        save();
    }

    @Step("Создание статического ключа")
    public void createStaticKeyNewStorage() {
        accessId = new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(new JSONObject("{\"access_key\":{\"description\":\"Ключ\",\"product_name\":\"storage-new\"}}"))
                .post("/v1/projects/{}/service_accounts/{}/access_keys", projectId, id)
                .assertStatus(201)
                .jsonPath()
                .getString("data.access_id");
    }

    @Step("Удаление статического ключа")
    public void deleteStaticKeyNewStorage(String keyId) {
        new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .delete("/v1/projects/{}/service_accounts/{}/access_keys/{}?product_name=storage-new", projectId, id, keyId)
                .assertStatus(204);
    }

    @Step("Изменение сервисного аккаунта")
    public void editServiceAccount(String title, Role newRole) {
        JsonPath jsonPath = JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.service_account.policy.bindings.[0].role", newRole.toString())
                .set("$.service_account.title", title)
                .send(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .patch("/v1/projects/{}/service_accounts/{}", projectId, id)
                .assertStatus(200)
                .jsonPath();
        role = newRole;
        save();
        Assertions.assertTrue((Boolean) jsonPath.get(String.format("data.roles.any{it.name='%s'}", newRole)));
    }

    @Override
    @Step("Создание сервисного аккаунта")
    protected void create() {
        JsonPath jsonPath = new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(toJson())
                .post("/v1/projects/{}/service_accounts", projectId)
                .assertStatus(201)
                .jsonPath();

        Assertions.assertEquals(title, jsonPath.get("data.title"));
        id = jsonPath.get("data.name");

        if (withApiKey) {
            jsonPath = new Http(Configure.iamURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .body(toJson())
                    .post("/v1/projects/{}/service_accounts/{}/api_keys", projectId, id)
                    .assertStatus(201)
                    .jsonPath();
            secret = jsonPath.get("data.client_secret");
        }
    }

    @Override
    @Step("Удаление сервисного аккаунта")
    protected void delete() {
        if (withApiKey) {
            new Http(Configure.iamURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .delete("/v1/projects/{}/service_accounts/{}/api_keys/{}", projectId, id, id)
                    .assertStatus(204);

            int counter = 6;
            JsonPath apiKeyResponse = null;
            log.info("Проверка статуса статического ключа");
            while (counter > 0) {
                sleep(10000);
                apiKeyResponse = new Http(Configure.iamURL)
                        .setRole(Role.CLOUD_ADMIN)
                        .get("/v1/projects/{}/service_accounts/{}", projectId, id)
                        .assertStatus(200)
                        .jsonPath();


                if (apiKeyResponse.get("data.api_keys") == null) {
                    break;
                }
                counter = counter - 1;
            }

            Assertions.assertNull(apiKeyResponse.get("data.api_key"));
        }

        new Http(Configure.iamURL)
                .setRole(Role.CLOUD_ADMIN)
                .delete("/v1/projects/{}/service_accounts/{}", projectId, id)
                .assertStatus(204);
    }
}
