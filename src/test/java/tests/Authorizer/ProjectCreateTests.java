package tests.Authorizer;

import io.qameta.allure.Description;
import models.orderService.Rhel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.authorizer.ProjectSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Набор тестов по проектам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(500)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectCreateTests extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    ProjectSteps projectSteps = new ProjectSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Создание проекта")
    @MethodSource("dataFolders")
    @Description("Создание проекта с сохранением в Shared Memory")
    public void createProject(String env) {
        testVars.setVariables("token", keyCloakSteps.getToken());
        projectSteps.createProject("FOLDER", "PROJECT_"+env, env);
    }

    static Stream<Arguments> dataFolders() {
        return Stream.of(Arguments.arguments("DEV"), Arguments.arguments("TEST"));
    }

}
