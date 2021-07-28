package tests.Authorizer;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.authorizer.ProjectSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор тестов по проектам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(99999)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectDeleteTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    ProjectSteps projectSteps = new ProjectSteps();

    @Test
    @Order(2)
    @DisplayName("Удаление проекта")
    @Description("Удаление проекта с сохранением в Shared Memory")
    public void deleteDepartmentBlock() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        projectSteps.deleteProject("PROJECT_DEV");
    }
}
