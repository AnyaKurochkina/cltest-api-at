package models.authorizer;

import com.mifmif.common.regex.Generex;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.authorizer.ProjectSteps;
import steps.portalBack.PortalBackSteps;

import java.util.Objects;

@Builder
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Project extends Entity {
    @ToString.Include
    public String id;
    public String informationSystem;
    public ProjectEnvironmentPrefix projectEnvironmentPrefix;
    public String projectName;
    public Boolean isForOrders;
    public String prefix;

    transient String folderName;


    public Entity init() {
        if (informationSystem == null) {
            informationSystem = ((InformationSystem) InformationSystem.builder().build().createObject()).getId();
        }
        if (projectEnvironmentPrefix == null) {
            projectEnvironmentPrefix = PortalBackSteps.getProjectEnvironmentPrefix("DEV", informationSystem);
        }
        if (folderName == null) {
            folderName = ((Folder) Folder.builder().kind(Folder.DEFAULT).build().createObject()).getName();
        }
        if (prefix == null) {
            prefix = projectEnvironmentPrefix.getId();
        }
        if (projectName == null) {
            projectName = new Generex("project [0-9a-zA-Z]{5,15}").random();
        }
        return this;
    }

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/structure/create_project.json")
                .set("$.project.title", projectName)
                .set("$.project.information_system_id", Objects.requireNonNull(informationSystem))
                .set("$.project.project_environment_id", Objects.requireNonNull(projectEnvironmentPrefix.getProjectEnvironmentId()))
                .set("$.project.environment_prefix_id", Objects.requireNonNull(prefix))
                .build();
    }

    public void edit() {
        String projectNameNew = new Http(Configure.AuthorizerURL)
                .body("{\"project\":{\"title\":\"" + projectName + "\"}}")
                .patch(String.format("projects/%s", id))
                .assertStatus(200)
                .jsonPath()
                .getString("data.title");
        Assertions.assertEquals(projectName, projectNameNew, "Title проекта не изменился");
        setProjectName(projectNameNew);
    }

    @Override
    @Step("Создание проекта")
    protected void create() {
        id = new Http(Configure.AuthorizerURL)
                .body(toJson())
                .post(String.format("folders/%s/projects", folderName))
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }

    @Override
    @Step("Удаление проекта")
    protected void delete() {
        new Http(Configure.AuthorizerURL)
                .delete("projects/" + id)
                .assertStatus(204);
    }
}
