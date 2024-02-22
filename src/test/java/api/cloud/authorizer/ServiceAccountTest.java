package api.cloud.authorizer;

import api.Tests;
import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.authorizer.ServiceAccount;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;
import steps.keyCloak.KeyCloakSteps;

import static core.helper.Configure.resourceManagerURL;

@Epic("Управление")
@Feature("Сервисные аккаунты")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceAccountTest extends Tests {

    @Test
    @Order(1)
    @TmsLink("376600")
    @DisplayName("Создание сервисного аккаунта")
    void createServiceAccount() {
        try (ServiceAccount account = ServiceAccount.builder().title("deleteServiceAccount").build().createObjectExclusiveAccess()) {
            String token = KeyCloakSteps.getNewToken(account);
            new Http(resourceManagerURL)
                    .setWithoutToken().addHeader("Authorization", "bearer " + token)
                    .get("/v1/organizations")
                    .assertStatus(200);
        }
    }

    @Test
    @Order(2)
    @TmsLink("534443")
    @DisplayName("Редактирование сервисного аккаунта")
    void editServiceAccount() {
        String title = "deleteServiceAccount";
        try (ServiceAccount account = ServiceAccount.builder().title(title).build().createObjectExclusiveAccess()) {
            account.editServiceAccount(title, Role.ACCESS_GROUP_ADMIN);
        }
    }

    @Disabled
    @Test
    @Order(3)
    @TmsLink("534448")
    @DisplayName("Создание статического ключа досутпа hcp bucket")
    void createStaticKey() {
        Project projectDev = Project.builder().projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV")).isForOrders(true).build().createObject();
        try (ServiceAccount account = ServiceAccount.builder().projectId(projectDev.getId()).title("createServiceAccount").build().createObjectExclusiveAccess()) {
            account.createStaticKey();
            account.deleteStaticKey();
        }
    }

    @Disabled
    @Test
    @Order(4)
    @TmsLink("534451")
    @DisplayName("Удаление статического ключа досутпа hcp bucket")
    void deleteStaticKey() {
        Project projectDev = Project.builder().projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV")).isForOrders(true).build().createObject();
        try (ServiceAccount account = ServiceAccount.builder().projectId(projectDev.getId()).title("deleteServiceAccount").build().createObjectExclusiveAccess()) {
            account.createStaticKey();
            account.deleteStaticKey();
        }
    }

    @Test
    @Order(5)
    @MarkDelete
    @TmsLink("376604")
    @DisplayName("Удаление сервисного аккаунта")
    void deleteServiceAccount() {
        try(ServiceAccount account = ServiceAccount.builder().title("deleteServiceAccount").build().createObjectExclusiveAccess()) {
            account.deleteObject();
        }
    }
}



