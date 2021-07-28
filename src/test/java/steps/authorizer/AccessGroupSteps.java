package steps.authorizer;

import core.helper.Configurier;
import io.qameta.allure.Step;
import models.authorizer.AccessGroup;
import steps.Steps;
import models.authorizer.Project;

public class AccessGroupSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Создание группы доступа в проекте {projectName} с названием {accessName}")
    public void createAccessGroup(String projectName, String accessName) {
        Project project = cacheService.entity(Project.class)
                .setField("projectName", projectName)
                .getEntity();

        String groupName = jsonHelper.getJsonTemplate("/accessGroup/accessGroup.json")
                .set("$.access_group.name", accessName)
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
}
