package steps.portalBack;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import models.authorizer.Folder;
import models.authorizer.InformationSystem;
import models.authorizer.Organization;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import steps.Steps;

import static core.helper.Configure.PortalBackURL;

public class PortalBackSteps extends Steps {

    @SneakyThrows
    @Step("Получение ID project env")
    public ProjectEnvironment getProjectEnvironment(String envName, String informationSystemId) {
        String folderName = ((Folder) Folder.builder().kind(Folder.DEFAULT).build().createObject()).getName();

        JsonPath jsonPath = new Http(PortalBackURL)
                .get(String.format("folders/%s/information_systems/%s/project_environments?page=1&per_page=100&include=total_count", folderName, informationSystemId))
                .assertStatus(200)
                .jsonPath();

        int countOfIteration = (int)jsonPath.get("meta.total_count")/ 100 + 1;
        for (int i = 1; i <=countOfIteration; i++){
            JsonPath jsonPathInCycle = new Http(PortalBackURL)
                    .get(String.format("folders/%s/information_systems/%s/project_environments?page=%s&per_page=100&include=total_count", folderName, informationSystemId, i))
                    .assertStatus(200)
                    .jsonPath();

            String id = jsonPathInCycle.get(String.format("list.find{it.name.equals('%s')}.id", envName));
            String environmentType = jsonPathInCycle.get(String.format("list.find{it.name.equals('%s')}.environment_type", envName));
            if(id != null){
                return new ProjectEnvironment(id, environmentType, envName);
            }
        }
        throw new Exception("Не найден ProjectEnvironment с именем " + envName);
    }

    @Step("Получение пользователя из LDAP")
    public String getUsers(Project project, String username) {
        return  new Http(PortalBackURL)
                .get(String.format("users?q=%s&project_name=%s", username, project.getId()))
                .assertStatus(200)
                .jsonPath()
                .get("[0].unique_name");
    }

}
