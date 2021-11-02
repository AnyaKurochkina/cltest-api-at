package models.authorizer;

import core.helper.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import steps.Steps;
import steps.authorizer.AuthorizerSteps;
import tests.Tests;

@Builder
@Getter
public class Organization extends Entity {
    public String title;
    public String name;

    @Override
    public void init() {
        if(title == null)
            title = "ВТБ";
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
