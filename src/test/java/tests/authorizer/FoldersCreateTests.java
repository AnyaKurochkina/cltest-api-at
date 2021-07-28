package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор тестов для создания Орг структуры")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(300)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FoldersCreateTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @Test
    @Order(1)
    @DisplayName("Создание Бизнес-блока")
    @Description("Создание Бизнес-блока с сохранением в Shared Memory")
    public void createBusinessBlock() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        authorizerSteps.createFolder("business_block", "vtb", "BUSINESS_FOLDER");
    }

    @Test
    @Order(2)
    @DisplayName("Создание Департамента")
    @Description("Создание Департамента с сохранением в Shared Memory")
    public void createDepartmentBlock() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        authorizerSteps.createFolder("department", "BUSINESS_FOLDER", "DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @DisplayName("Создание Папки")
    @Description("Создание Папки с сохранением в Shared Memory")
    public void createFolder() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        authorizerSteps.createFolder("default", "DEPARTMENT_FOLDER", "FOLDER");
    }

}
