package tests.suites.Authorizer;

import core.helper.Configurier;
import io.qameta.allure.Step;
import tests.suites.Authorizer.models.AccessGroup;
import tests.suites.Steps;
import tests.suites.Authorizer.models.Project;

public class AccessGroupSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Создание группы доступа в проекте {projectName} с названием {accessName}")
    public void createAccessGroup(String projectName, String accessName) {
        Project project = cacheService.entity(Project.class)
                .setField("projectName", projectName)
                .getEntity();

        String groupName = jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.access_group.name", projectName)
                .set("$.access_group.project_name", project.id)
                .send(URL)
                .post(String.format("portal/api/v1/projects/%s/access_groups", project.id))
                .assertStatus(201)
                .jsonPath()
                .get("name");

        AccessGroup accessGroup = AccessGroup.builder()
                .name(groupName)
                .projectName(project.id)
                .build();
        cacheService.saveEntity(AccessGroup.class, accessGroup);
    }
}
