package tests.authorizer;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import tests.Tests;

@DisplayName("Набор тестов для удаления Орг структуры")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.authorizer.FoldersDeleteTests")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FoldersDeleteTests implements Tests {
    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @Test
    @Order(4)
    @TmsLink("24")
    @DisplayName("Удаление Папки")
    @Description("Удаление Папки с сохранением в Shared Memory")
    public void deleteFolder() {
        authorizerSteps.deleteFolder("default", "FOLDER");
    }

    @Test
    @Order(5)
    @TmsLink("25")
    @DisplayName("Удаление Департамента")
    @Description("Удаление Департамента с сохранением в Shared Memory")
    public void deleteDepartmentBlock() {
        authorizerSteps.deleteFolder("department", "DEPARTMENT_FOLDER");
    }

    @Test
    @Order(6)
    @TmsLink("26")
    @DisplayName("Удаление Бизнес-блока")
    @Description("Удаление Бизнес-блока с измениенемм состояния в Shared Memory")
    public void deleteBusinessBlock() {
        authorizerSteps.deleteFolder("business_block", "BUSINESS_FOLDER");
    }
}

