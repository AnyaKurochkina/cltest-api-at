package tests.Authorizer;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.Authorizer.ProjectSteps;
import steps.KeyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор тестов по проектам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(3)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    ProjectSteps projectSteps = new ProjectSteps();

    @Test
    @Order(1)
    @DisplayName("Создание проекта")
    @Description("Создание проекта с сохранением в Shared Memory")
    public void createProject() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        projectSteps.createProject("FOLDER", "PROJECT_DEV");
    }

}
