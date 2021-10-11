package steps.authorizer;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import models.authorizer.Project;
import models.authorizer.ServiceAccount;
import steps.Steps;

public class ServiceAccountSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    /**
     * @param projectName имя проекта
     */
    @Step("Создание сервисного аккаунта в проекте {projectName}")
    public void createServiceAccount(String projectName) {
        //Получение проекта по его имени
        Project project = cacheService.entity(Project.class)
                .withField("projectName", projectName)
                .forOrders(false)
                .getEntity();
        //Отправление запроса на создание сервисного аккаунта
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/authorizer/service_accounts.json")
                .set("$.service_account.title", project.id)
                .send(URL)
                .post(String.format("authorizer/api/v1/projects/%s/service_accounts", project.id))
                .assertStatus(201)
                .jsonPath();
        //Создание сервисного аккаунта с параметрами полученными выше
        ServiceAccount serviceAccount = ServiceAccount.builder()
                .name(jsonPath.get("data.name"))
                .projectName(project.id)
                .secret(jsonPath.get("data.client_secret"))
                .build();
        //Сохранение сервисного аккаунта
        cacheService.saveEntity(serviceAccount);
    }

    /**
     * @param projectName имя проекта
     */
    @Step("Удаление сервисного аккаунта в проекте {projectName}")
    public void deleteServiceAccount(String projectName) {
        //Получегие проекта по его имени
        Project project = cacheService.entity(Project.class)
                .withField("projectName", projectName)
                .forOrders(false)
                .getEntity();
        //Получение сервисного аккаунта по ID проекта
        ServiceAccount serviceAccount = cacheService.entity(ServiceAccount.class)
                .withField("projectId", project.id)
                .getEntity();
        //Отправка запроса на удаление сервисного аккаунта
        new Http(URL)
                .delete(String.format("authorizer/api/v1/projects/%s/service_accounts/%s", project.id, serviceAccount.name))
                .assertStatus(204);
        //Проставление флага "Сервисный аккаунт удалён"
//        serviceAccount.isDeleted = true;
        //Сохранение текущего состояния сервисного аккаунта
        cacheService.saveEntity(serviceAccount);
    }


}
