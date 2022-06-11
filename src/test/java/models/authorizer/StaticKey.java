package models.authorizer;

import com.mifmif.common.regex.Generex;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;

import static core.utils.Waiting.sleep;

@Builder
@Getter
@Log4j2
public class StaticKey extends Entity {

    String projectId;
    String jsonTemplate;
    String serviceAccountName;
    ServiceAccount serviceAccount;

    @Override
    public Entity init() {
        jsonTemplate = "/authorizer/static_key.json";
        serviceAccount = ServiceAccount.builder().title("forStaticKey").build().createObject();
        if (serviceAccountName == null)
            serviceAccountName = serviceAccount.getId();
        projectId = serviceAccount.getProjectId();
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate).build();
    }

    @Override
    protected void create() {
        new Http(Configure.IamURL)
                .body(toJson())
                .post("/v1/projects/{}/service_accounts/{}/access_keys",
                        projectId,
                        serviceAccountName)
                .assertStatus(201)
                .jsonPath();

        int counter = 6;
        JsonPath jsonPath;
        log.info("Проверка статуса статического ключа");
        while (counter > 0) {
            sleep(10000);
            jsonPath = new Http(Configure.IamURL)
                    .get("/v1/projects/{}/service_accounts/{}/access_keys", projectId, serviceAccountName)
                    .assertStatus(200)
                    .jsonPath();

            log.info("Статус статического ключа: " + jsonPath.get("data.status").toString());
            if (jsonPath.get("data.status").equals(Collections.singletonList("active"))){
                break;
            }
            counter = counter - 1;
        }
        JsonPath jsonPathStatus = new Http(Configure.IamURL)
                .get("/v1/projects/{}/service_accounts/{}/access_keys", projectId, serviceAccountName)
                .assertStatus(200)
                .jsonPath();

        Assertions.assertEquals(Collections.singletonList("active"), jsonPathStatus.get("data.status"),
                "Статический ключ не создался, текущий статус: " + jsonPathStatus.get("data.status"));
    }

    @Override
    protected void delete() {
        new Http(Configure.IamURL)
                .delete("/v1/projects/{}/service_accounts/{}/access_keys/{}", projectId, serviceAccountName, serviceAccountName)
                .assertStatus(204);

        String keyStatus = "";
        int counter = 6;
        JsonPath jsonPath = null;
        log.info("Проверка статуса статического ключа");
        while (counter > 0) {
            sleep(30000);
            jsonPath = new Http(Configure.IamURL)
                    .get("/v1/projects/{}/service_accounts/{}/access_keys", projectId, serviceAccountName)
                    .assertStatus(200)
                    .jsonPath();

            log.info("Статус статического ключа: " + jsonPath.get("data.status").toString());
            if (jsonPath.getList("data").isEmpty()){
                break;
            }
            counter = counter - 1;
        }
        log.info("Итоговый статус статического ключа " + keyStatus);

        Assertions.assertTrue(jsonPath.getList("data").isEmpty(),
                "При удалении статического ключа ожидается в ответе пустой блок data, но data:\n" + jsonPath.getList("data").toString());
        save();
    }
}
