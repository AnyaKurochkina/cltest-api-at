package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.authorizer.ProjectSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Удаление проектов")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.ProjectDeleteTests")
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectDeleteTests implements Tests {
    ProjectSteps projectSteps = new ProjectSteps();

    @ParameterizedTest
    @MethodSource("dataFolders")
    @Order(2)
    @DisplayName("Удаление проекта")
    @Description("Удаление проекта с сохранением в Shared Memory")
    public void deleteDepartmentBlock(String env) {
        projectSteps.deleteProject(env);
    }

    static Stream<Arguments> dataFolders() {
        return Stream.of(Arguments.arguments("DEV"), Arguments.arguments("TEST"));
    }
}
