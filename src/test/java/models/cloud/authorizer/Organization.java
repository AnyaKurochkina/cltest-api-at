package models.cloud.authorizer;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

import static core.helper.Configure.resourceManagerURL;

@Builder
@Getter
public class Organization extends Entity {
    String title;
    String name;
    String type;

    @Override
    public Entity init() {
//        if(title == null)
//            title = "ВТБ";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    @Step("Получение организации")
    protected void create() {
        JsonPath path = new Http(resourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/organizations?page=1&per_page=25")
                .assertStatus(200)
                .jsonPath();
        name = path.getString("data[0].name");
        title = path.getString("data[0].title");
//                .getString(String.format("data.find{it.title=='%s'}.name", title));
    }

    @Override
    protected void delete() {}

}
