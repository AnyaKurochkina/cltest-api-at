package tests.authorizer;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
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
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectDeleteTests extends Tests {
    ProjectSteps projectSteps = new ProjectSteps();

    @ParameterizedTest(name = "{0}")
    @Source(ProductArgumentsProvider.ENV)
    @Order(2)
    @TmsLink("23")
    @DisplayName("Удаление проекта")
    @Description("Удаление проекта с сохранением в Shared Memory")
    public void deleteDepartmentBlock(String env) {
        projectSteps.deleteProject(env);
    }


}
