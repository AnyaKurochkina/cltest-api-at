package tests.accountManager;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.accountManager.AccountDeleteSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор для удаления счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(99999)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDeleteTest extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    AccountDeleteSteps accountDeleteSteps = new AccountDeleteSteps();

    @Test
    @Order(1)
    @DisplayName("Удаление счета для папки")
    @Description("Удаление счета папки")
    public void DeleteAccount() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountDeleteSteps.deleteAccount("FOLDER");
    }

    @Test
    @Order(2)
    @DisplayName("Удаление счета для папки Department")
    @Description("Удаление счета папки Департамент")
    public void DeleteAccountDepartment() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountDeleteSteps.deleteAccount("DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @DisplayName("Удаление счета для папки Business block")
    @Description("Удаление счета папки Бизнес блок")
    public void DeleteAccountBusinessBlock() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountDeleteSteps.deleteAccount("BUSINESS_FOLDER");
    }

}
