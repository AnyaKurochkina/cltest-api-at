package tests.AccountManager;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.accountManager.AccountCreate;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор для создания счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(400)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountCreateTest extends Tests {
    KeyCloakSteps keyCloakSteps = new KeyCloakSteps();
    AccountCreate accountCreate = new AccountCreate();

    @Test
    @Order(1)
    @DisplayName("Создание счета для папки Бизнес блок")
    @Description("Создание счета папки Бизнес блок с сохранением в Shared Memory")
    public void createAccountBusinessBlock() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountCreate.createAccount("BUSINESS_FOLDER");
    }

    @Test
    @Order(2)
    @DisplayName("Создание счета для папки Департамент")
    @Description("Создание счета папки Департамент с сохранением в Shared Memory")
    public void createAccountDepartment() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountCreate.createAccount("DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @DisplayName("Создание счета для папки")
    @Description("Создание счета папки с сохранением в Shared Memory")
    public void createAccount() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountCreate.createAccount("FOLDER");
    }

    @Test
    @Order(4)
    @DisplayName("Перевод денег с VTB на дочерний счет папки")
    @Description("Перевод денег с VTB на дочерний счет папки")
    public void transferMoneyFromAccountToFolder() {
        testVars.setVariables("token", keyCloakSteps.getToken());
        accountCreate.transferMoneyFromAccountToFolder("vtb", "FOLDER", "25000.00");
    }

}