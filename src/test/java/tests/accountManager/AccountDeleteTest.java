package tests.accountManager;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.accountManager.AccountSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор для удаления счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.accountManager.AccountDeleteTest")
@Tags({@Tag("regress"), @Tag("orgStructure")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDeleteTest implements Tests {
    AccountSteps accountSteps = new AccountSteps();

    @Test
    @Order(1)
    @DisplayName("Удаление счета для папки")
    @Description("Удаление счета папки")
    public void DeleteAccount() {
        accountSteps.deleteAccount("FOLDER");
    }

    @Test
    @Order(2)
    @DisplayName("Удаление счета для папки Department")
    @Description("Удаление счета папки Департамент")
    public void DeleteAccountDepartment() {
        accountSteps.deleteAccount("DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @DisplayName("Удаление счета для папки Business block")
    @Description("Удаление счета папки Бизнес блок")
    public void DeleteAccountBusinessBlock() {
        accountSteps.deleteAccount("BUSINESS_FOLDER");
    }

}
