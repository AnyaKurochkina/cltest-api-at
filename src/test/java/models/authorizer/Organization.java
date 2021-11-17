package models.authorizer;

import core.helper.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import org.json.JSONObject;
import steps.authorizer.AuthorizerSteps;

@Builder
@Getter
public class Organization extends Entity {
    public String title;
    public String name;

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
        name = new Http(AuthorizerSteps.URL)
                .get("authorizer/api/v1/organizations?page=1&per_page=25")
                .assertStatus(200)
                .jsonPath()
                .getString(String.format("data.find{it.title=='%s'}.name", title));
    }

}
