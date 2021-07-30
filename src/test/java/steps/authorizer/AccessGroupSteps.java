package steps.authorizer;

import core.helper.Configurier;
import core.helper.Http;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import steps.Steps;
import models.authorizer.Project;

import java.util.Random;

@Log4j2
public class AccessGroupSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Создание группы доступа в проекте {projectName} с названием {accessName}")
    public void createAccessGroup(String projectName, String accessName) {
        StringUtils stringUtils = new StringUtils();
        Project project = cacheService.entity(Project.class)
                .setField("projectName", projectName)
                .getEntity();

        String accessNameBuild = accessName + stringUtils.getRandString(12);
        String groupName = jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.access_group.name", accessNameBuild)
                .set("$.access_group.project_name", project.id)
                .send(URL)
                .post(String.format("portal/api/v1/projects/%s/access_groups", project.id))
                .assertStatus(201)
                .jsonPath()
                .get("name");

        models.authorizer.AccessGroup accessGroup = models.authorizer.AccessGroup.builder()
                .name(groupName)
                .projectName(project.id)
                .build();
        cacheService.saveEntity(accessGroup);
    }

    @Step("Удаление группы доступа в проекте {projectName}")
    public void deleteAccessGroup(String env) {
        StringUtils stringUtils = new StringUtils();
        Project project = cacheService.entity(Project.class)
                .setField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .setField("projectName", project.id)
                .getEntity();
        JsonPath jsonPath = new Http(URL)
                .delete(String.format("portal/api/v1/projects/%s/access_groups/%s", project.id, accessGroup.name))
                .assertStatus(204)
                .jsonPath();

        accessGroup.isDeleted = true;
        cacheService.saveEntity(accessGroup);
    }

}
