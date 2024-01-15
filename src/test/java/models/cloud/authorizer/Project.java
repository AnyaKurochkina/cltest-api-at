package models.cloud.authorizer;

import com.mifmif.common.regex.Generex;
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
import org.junit.jupiter.api.Assertions;
import steps.portalBack.PortalBackSteps;

import java.util.Objects;

@Builder
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Log4j2
public class Project extends Entity {
    @ToString.Include
    public String id;
    public String informationSystem;
    public ProjectEnvironmentPrefix projectEnvironmentPrefix;
    public String projectName;
    public Boolean isForOrders;

    transient String folderName;

    public Entity init() {
        if (!Configure.isT1()) {
            if (informationSystem == null) {
                informationSystem = ((InformationSystem) InformationSystem.builder().build().createObject()).getId();
            }
            if (projectEnvironmentPrefix == null) {
                projectEnvironmentPrefix = PortalBackSteps.getProjectEnvironmentPrefix("DEV", informationSystem);
            }
            if (projectEnvironmentPrefix.getProjectEnvironmentId() == null) {
                ProjectEnvironmentPrefix pe = PortalBackSteps.getProjectEnvironmentPrefixByEnv(projectEnvironmentPrefix.getEnv(), informationSystem);
                projectEnvironmentPrefix.setProjectEnvironmentId(pe.getProjectEnvironmentId());
                projectEnvironmentPrefix.setEnvType(pe.getEnvType());
                projectEnvironmentPrefix.setRisName(pe.getRisName());
                projectEnvironmentPrefix.setId(pe.getId());
                projectEnvironmentPrefix.setDescription(pe.getDescription());
            }
        }
        if (folderName == null) {
            folderName = ((Folder) Folder.builder().kind(Folder.DEFAULT).build().createObject()).getName();
        }
        if (projectName == null) {
            projectName = new Generex("project [0-9a-zA-Z]{5,15}").random();
        }
        return this;
    }

    public JSONObject toJson() {
        if (Configure.isT1())
            return JsonHelper.getJsonTemplate("/structure/create_project.json")
                    .set("$.project.title", projectName)
                    .put("$.project", "environment_type", "prod")
                    .remove("$.project.information_system_id")
                    .remove("$.project.project_environment_id")
                    .remove("$.project.environment_prefix_id")
                    .build();
        return JsonHelper.getJsonTemplate("/structure/create_project.json")
                .set("$.project.title", projectName)
                .set("$.project.information_system_id", Objects.requireNonNull(informationSystem))
                .set("$.project.project_environment_id", Objects.requireNonNull(projectEnvironmentPrefix.getProjectEnvironmentId()))
                .set("$.project.environment_prefix_id", Objects.requireNonNull(projectEnvironmentPrefix.getId()))
                .build();
    }

    public void edit() {
        String projectNameNew = new Http(Configure.resourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .body("{\"project\":{\"title\":\"" + projectName + "\"}}")
                .patch(String.format("/v1/projects/%s", id))
                .assertStatus(200)
                .jsonPath()
                .getString("data.title");
        Assertions.assertEquals(projectName, projectNameNew, "Title проекта не изменился");
        setProjectName(projectNameNew);
    }

    @Override
    @Step("Создание проекта")
    protected void create() {
        if (Objects.nonNull(isForOrders))
            if (isForOrders)
                Assertions.fail("Попытка создать isForOrders проект");
        id = new Http(Configure.resourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .body(toJson())
                .post(String.format("/v1/folders/%s/projects", folderName))
                .assertStatus(201)
                .jsonPath()
                .getString("data.name");
    }

    @Override
    @Step("Удаление проекта")
    protected void delete() {
        new Http(Configure.resourceManagerURL)
                .setRole(Role.CLOUD_ADMIN)
                .delete("/v1/projects/" + id)
                .assertStatus(204);
    }
}
