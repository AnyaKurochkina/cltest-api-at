package steps.authorizer;

import core.helper.Configurier;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import org.junit.Assert;
import models.authorizer.Folder;
import models.authorizer.Project;
import steps.Steps;

import java.util.List;
import java.util.Random;

public class ProjectSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Создание проекта в папке {folderName} с названием {projectName}")
    public void createProject(String folderName, String projectName) {
        Folder folder = cacheService.entity(Folder.class)
                .setField("name", folderName)
                .setField("isDeleted", false)
                .getEntity();

        String infoSystems = jsonHelper.getTestDataFieldValue("structure/projectEnvironmentsDEV.json", "DEV", "information_systems");
        String projectEnvId = jsonHelper.getTestDataFieldValue("structure/projectEnvironmentsDEV.json", "DEV", "project_environment_id");
        String prefix = getPrefixEnv(folder.id, infoSystems, projectEnvId);

        String projectId = jsonHelper.getJsonTemplate("/structure/create_project.json")
                .set("$.project.title", projectName)
                .set("$.project.information_system_id", infoSystems)
                .set("$.project.project_environment_id", projectEnvId)
                .set("$.project.environment_prefix_id", prefix)
                .send(URL)
                .post(String.format("authorizer/api/v1/folders/%s/projects", folder.id))
                .assertStatus(201)
                .jsonPath()
                .get("data.name");

        Project project = Project.builder()
                .projectName(projectName)
                .id(projectId)
                .informationSystem(infoSystems)
                .env("DEV")
                .build();
        cacheService.saveEntity(project);
    }

    @Step("Удаление проекта с названием {name}")
    public void deleteProject(String name) {
        Project project = cacheService.entity(Project.class)
                .setField("projectName", name)
                .getEntity();

        new Http(URL)
                .delete("authorizer/api/v1/projects/" + project.id)
                .assertStatus(204);

        project.isDeleted = true;
        cacheService.saveEntity(project);
    }


    public String getPrefixEnv(String projectId, String infoSystems, String projectEnvId) {
        JsonPath jsonPath = new Http(URL)
                .get(String.format("portal/api/v1/folders/%s/information_systems/%s/environment_prefixes?project_environment_id=%s&reserved=false", projectId, infoSystems, projectEnvId))
                .assertStatus(200)
                .jsonPath();
        List prefix_list = jsonPath.get("list");
        Random rand = new Random();
        int i = 0;
        try {
            i = rand.nextInt(prefix_list.size());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            Assert.fail("Количество префиксов <=0");
        }
        String prefix = jsonPath.get("list[" + i + "].id");
        return prefix;
    }

}
