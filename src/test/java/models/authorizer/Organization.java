package models.authorizer;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

import static core.helper.Configure.IamURL;
import static core.helper.Configure.ResourceManagerURL;

@Builder
@Getter
public class Organization extends Entity {
    String title;
    String name;

    @Override
    public Entity init() {
        if(title == null)
            title = "ВТБ";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    @Step("Получение организации")
    protected void create() {
        name = new Http(ResourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/organizations?page=1&per_page=25")
                .assertStatus(200)
                .jsonPath()
                .getString("data[0]");
//                .getString(String.format("data.find{it.title=='%s'}.name", title));
    }

    @Override
    protected void delete() {}

}
