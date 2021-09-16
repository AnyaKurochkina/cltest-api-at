package tests.accountManager;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.accountManager.AccountSteps;
import tests.Tests;

@DisplayName("Набор для удаления счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.accountManager.AccountDeleteTest")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDeleteTest extends Tests {
    AccountSteps accountSteps = new AccountSteps();

    @Test
    @Order(1)
    @TmsLink("27")
    @DisplayName("Удаление счета для папки")
    @Description("Удаление счета папки")
    public void DeleteAccount() {
        accountSteps.deleteAccount("FOLDER");
    }

    @Test
    @Order(2)
    @TmsLink("28")
    @DisplayName("Удаление счета для папки Department")
    @Description("Удаление счета папки Департамент")
    public void DeleteAccountDepartment() {
        accountSteps.deleteAccount("DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @TmsLink("29")
    @DisplayName("Удаление счета для папки Business block")
    @Description("Удаление счета папки Бизнес блок")
    public void DeleteAccountBusinessBlock() {
        accountSteps.deleteAccount("BUSINESS_FOLDER");
    }

}
