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
import models.EntityOld;
import org.json.JSONObject;

import java.util.List;

@Builder
@Getter
public class ServiceAccount extends Entity {
    public String projectName;
    public String secret;
    public String name;
    @Singular
    public List<String> roles;
    public Boolean isForOrders;

    @Override
    public void init() {
        if (name == null)
            name = new RandomStringGenerator().generateByRegex("[a-z]{5,18}");
        if (projectName == null)
            projectName = ((Project) Project.builder().isForOrders(false).build().createObject()).getId();
    }

    public JSONObject toJson() {
        return jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.service_account.title", name)
                .build();
    }

    @Override
    @Step("Создание сервисного аккаунта")
    public void create() {
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/authorizer/service_accounts.json")
                .set("$.service_account.title", projectName)
                .send(Configure.AuthorizerURL)
                .post(String.format("projects/%s/service_accounts", projectName))
                .assertStatus(201)
                .jsonPath();
        name = jsonPath.get("data.name");
        secret = jsonPath.get("data.client_secret");
    }

    @Override
    @Step("Удаление сервисного аккаунта")
    public void delete() {
        new Http(Configure.AuthorizerURL)
                .delete(String.format("projects/%s/service_accounts/%s", projectName, name))
                .assertStatus(204);
    }
}
