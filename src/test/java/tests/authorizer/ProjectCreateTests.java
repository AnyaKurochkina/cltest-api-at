package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
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
@OrderLabel("tests.authorizer.ProjectCreateTests")
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectCreateTests implements Tests {
    ProjectSteps projectSteps = new ProjectSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Создание проекта")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Создание проекта с сохранением в Shared Memory")
    public void createProject(String env) {
        projectSteps.createProject("FOLDER", "PROJECT_"+env, env);
    }


}
