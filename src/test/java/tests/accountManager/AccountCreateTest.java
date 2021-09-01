package tests.accountManager;

import io.qameta.allure.Description;
import org.junit.OrderLabel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import steps.accountManager.AccountSteps;
import tests.Tests;

@DisplayName("Набор для создания счета")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OrderLabel("tests.accountManager.AccountCreateTest")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("rhel")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountCreateTest implements Tests {
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
        accountCreate.transferMoneyFromOrganizationToFolder("ВТБ", "BUSINESS_FOLDER", "10000.00");
    }

    @Test
    @Order(5)
    @DisplayName("Перевод денег со счета папки Бизнес блок на счет папки Департамент")
    @Description("Перевод денег со счета папки Бизнес блок на счет папки Департамент")
    public void transferMoneyFromAccountToDepartment() {
        accountCreate.transferMoneyFromFolderToFolder("BUSINESS_FOLDER", "DEPARTMENT_FOLDER", "10000.00");
    }

    @Test
    @Order(6)
    @DisplayName("Перевод денег со счета папки Департамент на счет папки")
    @Description("Перевод денег со счета папки Департамент на счет папки")
    public void transferMoneyFromAccountToFolder() {
        accountCreate.transferMoneyFromFolderToFolder("DEPARTMENT_FOLDER","FOLDER", "10000.00");
    }

}