package models.authorizer;

import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.Objects;

@Builder
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Log4j2
public class T1Project extends Entity {
    public String projectName;
    public String id;

    @Override
    public Entity init() {
        return null;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/structure/create_projectt1.json")
                .set("$.project.title", projectName)
               .build();
    }

    @Override
    @Step("Создание проекта")
    protected void create() {
        id = new Http(Configure.ResourceManagerURL)
                .body(toJson())
                .setRole(Role.T1ADMIN)
                .post(String.format("v1/organizations/org1/projects"))
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }

    @Override
    @Step("Удаление проекта")
    protected void delete() {
        new Http(Configure.IamURL)
                .delete("/v1/projects/" + id)
                .assertStatus(204);
    }
}
