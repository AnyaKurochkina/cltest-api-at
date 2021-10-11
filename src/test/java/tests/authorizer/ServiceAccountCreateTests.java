package tests.authorizer;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.AccessGroup;
import models.authorizer.ServiceAccount;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.authorizer.ServiceAccountSteps;
import tests.Tests;

@Epic("Управление")
@Feature("Сервисные аккаунты")
@Tags({@Tag("regress"), @Tag("orgStructure3"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class ServiceAccountCreateTests extends Tests {

    @Test
    @Order(1)
    @DisplayName("Создание сервисного аккаунта")
    public void createServiceAccount() {
        ServiceAccount.builder().isForOrders(false).build().createObject();
    }

    @Test
    @Order(2)
    @DisplayName("Удаление сервисного аккаунта")
    public void deleteServiceAccount() {
        ServiceAccount.builder().isForOrders(false).build().createObject();
    }

}



