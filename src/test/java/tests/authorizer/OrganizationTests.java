package tests.authorizer;


import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import tests.Tests;

@DisplayName("Набор тестов по организации")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.OrganizationTests")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrganizationTests implements Tests {
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();


    @Order(1)
    @Test
    @DisplayName("Получение организации")
    @Description("Получение организации с сохранением в Shared Memory")
    public void createProject() {
        authorizerSteps.getOrgName("ВТБ");
    }

}
