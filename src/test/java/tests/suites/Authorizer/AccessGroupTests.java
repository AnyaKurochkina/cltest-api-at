package tests.suites.Authorizer;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import tests.suites.KeyCloak.KeyCloakSteps;
import tests.suites.Tests;

@DisplayName("Набор тестов по группам доступа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(4)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessGroupTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    AccessGroupSteps accessGroupSteps = new AccessGroupSteps();

    @Test
    @Order(1)
    @DisplayName("Создание Группы доступа")
    @Description("Создание Группы доступа с сохранением в Shared Memory")
    public void createBusinessBlock() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accessGroupSteps.createAccessGroup("PROJECT_DEV", "ACCESS_GROUP");
    }
}