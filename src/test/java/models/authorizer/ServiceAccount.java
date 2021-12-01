package models.authorizer;

import core.helper.Configure;
import core.helper.Http;
import core.random.string.RandomStringGenerator;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import models.Entity;
import org.json.JSONObject;

import java.util.List;

@Builder
@Getter
public class ServiceAccount extends Entity {
    String projectId;
    String secret;
    String id;
    String title;

    @Singular
    public List<String> roles;

    @Override
    public Entity init() {
        if (id == null)
            id = new RandomStringGenerator().generateByRegex("[a-z]{5,18}");
        if (projectId == null)
            projectId = ((Project) Project.builder().isForOrders(false).build().createObject()).getId();
        return this;
    }

    public JSONObject toJson() {
        return jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.service_account.title", title)
                .build();
    }

    @Override
    @Step("Создание сервисного аккаунта")
    protected void create() {
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/authorizer/service_accounts.json")
                .set("$.service_account.title", projectId)
                .send(Configure.AuthorizerURL)
                .post(String.format("projects/%s/service_accounts", projectId))
                .assertStatus(201)
                .jsonPath();
        id = jsonPath.get("data.name");
        secret = jsonPath.get("data.client_secret");
    }

    @Override
    @Step("Удаление сервисного аккаунта")
    protected void delete() {
        new Http(Configure.AuthorizerURL)
                .delete(String.format("projects/%s/service_accounts/%s", projectId, id))
                .assertStatus(204);
    }
}
