package tests.AccountManager;

import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import steps.accountManager.AccountSteps;
import steps.keyCloak.KeyCloakSteps;
import tests.Tests;

@DisplayName("Набор для создания счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(400)
@Tag("regress")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountCreateTest extends Tests {
    AccountSteps accountCreate = new AccountSteps();

    @Test
    @Order(1)
    @DisplayName("Создание счета для папки Бизнес блок")
    @Description("Создание счета папки Бизнес блок с сохранением в Shared Memory")
    public void createAccountBusinessBlock() {
        accountCreate.createAccount("BUSINESS_FOLDER");
    }

    @Test
    @Order(2)
    @DisplayName("Создание счета для папки Департамент")
    @Description("Создание счета папки Департамент с сохранением в Shared Memory")
    public void createAccountDepartment() {
        accountCreate.createAccount("DEPARTMENT_FOLDER");
    }

    @Test
    @Order(3)
    @DisplayName("Создание счета для папки")
    @Description("Создание счета папки с сохранением в Shared Memory")
    public void createAccount() {
        accountCreate.createAccount("FOLDER");
    }

    @Test
    @Order(4)
    @DisplayName("Перевод денег со счета организации на счет папки Бизнес блок")
    @Description("Перевод денег со счета организации на счет папки Бизнес блок")
    public void transferMoneyFromAccountToBusinessBlock() {
        accountCreate.transferMoneyFromOrganizationToFolder("ВТБ", "BUSINESS_FOLDER", "1000.00");
    }

    @Test
    @Order(5)
    @DisplayName("Перевод денег со счета папки Бизнес блок на счет папки Департамент")
    @Description("Перевод денег со счета папки Бизнес блок на счет папки Департамент")
    public void transferMoneyFromAccountToDepartment() {
        accountCreate.transferMoneyFromFolderToFolder("BUSINESS_FOLDER", "DEPARTMENT_FOLDER", "1000.00");
    }

    @Test
    @Order(6)
    @DisplayName("Перевод денег со счета папки Департамент на счет папки")
    @Description("Перевод денег со счета папки Департамент на счет папки")
    public void transferMoneyFromAccountToFolder() {
        accountCreate.transferMoneyFromFolderToFolder("DEPARTMENT_FOLDER","FOLDER", "1000.00");
    }

}