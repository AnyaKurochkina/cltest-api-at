package steps.portalBack;

import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import models.authorizer.Folder;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import steps.Steps;

import java.util.Objects;

import static core.helper.Configure.PortalBackURL;

public class PortalBackSteps extends Steps {

    @SneakyThrows
    @Step("Получение ID project env")
    public ProjectEnvironment getProjectEnvironment(String envType, String informationSystemId) {
        String folderName = ((Folder) Folder.builder().kind(Folder.DEFAULT).build().createObject()).getName();

        JsonPath jsonPath = new Http(PortalBackURL)
                .get("folders/{}/information_systems/{}/project_environments?page=1&per_page=100&include=total_count", folderName, informationSystemId)
                .assertStatus(200)
                .jsonPath();

        int countOfIteration = (int)jsonPath.get("meta.total_count")/ 100 + 1;
        for (int i = 1; i <=countOfIteration; i++){
            JsonPath jsonPathInCycle = new Http(PortalBackURL)
                    .get("folders/{}/information_systems/{}/project_environments?page={}&per_page=100&include=total_count", folderName, informationSystemId, i)
                    .assertStatus(200)
                    .jsonPath();

            String id = jsonPathInCycle.get(String.format("list.find{it.environment_type.equals('%s')}.id", Objects.requireNonNull(envType)));
            String environmentType = jsonPathInCycle.get(String.format("list.find{it.environment_type.equals('%s')}.environment_type", envType));
            if(id != null){
                return new ProjectEnvironment(id, environmentType, envType);
            }
        }
        throw new Exception("Не найден ProjectEnvironment с именем " + envType);
    }

    @Step("Получение пользователя из LDAP")
    public String getUsers(Project project, String username) {
        return  new Http(PortalBackURL)
                .get("users?q={}&project_name={}", username, project.getId())
                .assertStatus(200)
                .jsonPath()
                .get("[0].unique_name");
    }

}
