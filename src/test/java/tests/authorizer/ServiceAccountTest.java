package tests.authorizer;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.ServiceAccount;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Управление")
@Feature("Сервисные аккаунты")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class ServiceAccountTest extends Tests {

    @Test
    @Order(1)
    @DisplayName("Создание сервисного аккаунта")
    void createServiceAccount() {
        ServiceAccount.builder().title("deleteServiceAccount").build().createObject();
    }

    @Test
    @Order(2)
    @MarkDelete
    @DisplayName("Удаление сервисного аккаунта")
    void deleteServiceAccount() {
        ServiceAccount.builder().title("deleteServiceAccount").build().createObject().deleteObject();
    }

}



