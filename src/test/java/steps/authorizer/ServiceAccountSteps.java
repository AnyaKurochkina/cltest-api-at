package steps.authorizer;

import core.helper.Configurier;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import models.authorizer.Project;
import models.authorizer.ServiceAccount;
import steps.Steps;

public class ServiceAccountSteps extends Steps {
    private static final String URL = Configurier.getAppProp("host_kong");


    @Step("Создание сервисного аккаунта в проекте {projectName}")
    public void createServiceAccount(String projectName) {
        Project project = cacheService.entity(Project.class)
                .withField("projectName", projectName)
                .getEntity();

        JsonPath jsonPath = jsonHelper.getJsonTemplate("/authorizer/service_accounts.json")
                .set("$.service_account.title", project.id)
                .send(URL)
                .post(String.format("authorizer/api/v1/projects/%s/service_accounts", project.id))
                .assertStatus(201)
                .jsonPath();

        ServiceAccount serviceAccount = ServiceAccount.builder()
                .name(jsonPath.get("data.name"))
                .projectId(project.id)
                .secret(jsonPath.get("data.client_secret"))
                .build();
        cacheService.saveEntity(serviceAccount);
    }

    @Step("Удаление сервисного аккаунта в проекте {projectName}")
    public void deleteServiceAccount(String projectName) {
        Project project = cacheService.entity(Project.class)
                .withField("projectName", projectName)
                .getEntity();
        ServiceAccount serviceAccount = cacheService.entity(ServiceAccount.class)
                .withField("projectId", project.id)
                .getEntity();
        new Http(URL)
                .delete(String.format("authorizer/api/v1/projects/%s/service_accounts/%s", project.id, serviceAccount.name))
                .assertStatus(204);
        serviceAccount.isDeleted = true;
        cacheService.saveEntity(serviceAccount);
    }


}
