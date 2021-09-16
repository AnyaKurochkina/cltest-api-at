package tests.authorizer;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import tests.Tests;

@DisplayName("Набор тестов для создания Орг структуры")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.FoldersCreateTests")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FoldersCreateTests extends Tests {
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @Test
    @Order(1)
    @TmsLink("8")
    @DisplayName("Создание Бизнес-блока")
    @Description("Создание Бизнес-блока с сохранением в Shared Memory")
    public void createBusinessBlock() {
        authorizerSteps.createFolder("business_block", "vtb", "BUSINESS_FOLDER");
    }

    @Test
    @Order(2)
    @TmsLink("9")
    @DisplayName("Создание Департамента")
    @Description("Создание Департамента с сохранением в Shared Memory")
    public void createDepartmentBlock() {
        authorizerSteps.createFolder("department", "BUSINESS_FOLDER", "DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @TmsLink("10")
    @DisplayName("Создание Папки")
    @Description("Создание Папки с сохранением в Shared Memory")
    public void createFolder() {
        authorizerSteps.createFolder("default", "DEPARTMENT_FOLDER", "FOLDER");
    }

}
