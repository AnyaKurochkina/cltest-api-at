package tests.AccountManager;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.accountManager.AccountDelete;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор для удаления счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(99999)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDeleteTest extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    AccountDelete accountDelete = new AccountDelete();

    @Test
    @Order(1)
    @DisplayName("Удаление счета для папки")
    @Description("Удаление счета папки")
    public void DeleteAccount() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountDelete.deleteAccount("FOLDER");
    }

    @Test
    @Order(2)
    @DisplayName("Удаление счета для папки Department")
    @Description("Удаление счета папки Департамент")
    public void DeleteAccountDepartment() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountDelete.deleteAccount("DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @DisplayName("Удаление счета для папки Business block")
    @Description("Удаление счета папки Бизнес блок")
    public void DeleteAccountBusinessBlock() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountDelete.deleteAccount("BUSINESS_FOLDER");
    }

}
