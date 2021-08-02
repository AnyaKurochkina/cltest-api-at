package tests.Authorizer;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор тестов для удаления Орг структуры")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(100000)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FoldersDeleteTests extends Tests {
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @Test
    @Order(4)
    @DisplayName("Удаление Папки")
    @Description("Удаление Папки с сохранением в Shared Memory")
    public void deleteFolder() {
        authorizerSteps.deleteFolder("default", "FOLDER");
    }

    @Test
    @Order(5)
    @DisplayName("Удаление Департамента")
    @Description("Удаление Департамента с сохранением в Shared Memory")
    public void deleteDepartmentBlock() {
        authorizerSteps.deleteFolder("department", "DEPARTMENT_FOLDER");
    }

    @Test
    @Order(6)
    @DisplayName("Удаление Бизнес-блока")
    @Description("Удаление Бизнес-блока с измениенемм состояния в Shared Memory")
    public void deleteBusinessBlock() {
        authorizerSteps.deleteFolder("business_block", "BUSINESS_FOLDER");
    }
}

