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

public class PortalBackSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");
    @Step("Получение информационных систем")
    public void getInfoSys(String sysName) {
        Organization organization = cacheService.entity(Organization.class)
                .getEntity();
        JsonPath jsonPath = new Http(URL)
                .get(String.format("portal/api/v1/organizations/%s/information_systems?page=1&per_page=100&include=total_count", organization.name))
                .assertStatus(200)
                .jsonPath();

        int countOfIteration = (int)jsonPath.get("meta.total_count")/ 100 + 1;

        for (int i = 1; i <=countOfIteration; i++){
            JsonPath jsonPathInCycle = new Http(URL)
                    .get(String.format("portal/api/v1/organizations/%s/information_systems?page=%s&per_page=100&include=total_count", organization.name, i))
                    .assertStatus(200)
                    .jsonPath();

            if(jsonPathInCycle.get(String.format("list.find{it.code=='%s'}.id", sysName)) != null){
                InformationSystem informationSystem = InformationSystem.builder()
                        .id(jsonPathInCycle.get(String.format("list.find{it.code=='%s'}.id", sysName)))
                        .build();
                cacheService.saveEntity(informationSystem);
                break;
            }
        }
    }

    @SneakyThrows
    @Step("Получение ID project env")
    public ProjectEnvironment getProjectEnvironment(String envName, String informationSystemId) {
        String folderName = ((Folder) Folder.builder().kind(Folder.DEFAULT).build().createObject()).getName();

        JsonPath jsonPath = new Http(URL)
                .get(String.format("portal/api/v1/folders/%s/information_systems/%s/project_environments?page=1&per_page=100&include=total_count", folderName, informationSystemId))
                .assertStatus(200)
                .jsonPath();

        int countOfIteration = (int)jsonPath.get("meta.total_count")/ 100 + 1;
        for (int i = 1; i <=countOfIteration; i++){
            JsonPath jsonPathInCycle = new Http(URL)
                    .get(String.format("portal/api/v1/folders/%s/information_systems/%s/project_environments?page=%s&per_page=100&include=total_count", folderName, informationSystemId, i))
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
        return  new Http(URL)
                .get(String.format("portal/api/v1/users?q=%s&project_name=%s", username, project.getId()))
                .assertStatus(200)
                .jsonPath()
                .get("[0].unique_name");
    }

}
