package tests.authorizer;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.authorizer.ProjectSteps;
import tests.Tests;

@DisplayName("Удаление проектов")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.ProjectDeleteTests")
@Tags({@Tag("regress"), @Tag("orgStructure")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectDeleteTests implements Tests {
    ProjectSteps projectSteps = new ProjectSteps();

    @ParameterizedTest
    @Source(ProductArgumentsProvider.ENV)
    @Order(2)
    @DisplayName("Удаление проекта")
    @Description("Удаление проекта с сохранением в Shared Memory")
    public void deleteDepartmentBlock(String env) {
        projectSteps.deleteProject(env);
    }


}
