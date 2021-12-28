package models.authorizer;

import core.helper.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;

import static core.helper.Configure.AuthorizerURL;

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
        name = new Http(AuthorizerURL)
                .get("organizations?page=1&per_page=25")
                .assertStatus(200)
                .jsonPath()
                .getString(String.format("data.find{it.title=='%s'}.name", title));
    }

}
