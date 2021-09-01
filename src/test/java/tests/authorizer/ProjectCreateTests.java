package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.authorizer.ProjectSteps;
import tests.Tests;

@DisplayName("Набор тестов по проектам")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.ProjectCreateTests")
@Tags({@Tag("regress"), @Tag("orgStructure")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectCreateTests implements Tests {
    ProjectSteps projectSteps = new ProjectSteps();

    @ParameterizedTest
    @Order(1)
    @DisplayName("Создание проекта")
    @Source(ProductArgumentsProvider.ENV)
    @Description("Создание проекта с сохранением в Shared Memory")
    public void createProject(String env, String tmsId) {
        projectSteps.createProject("FOLDER", "PROJECT_"+env, env);
    }


}
