package tests.accountManager;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.accountManager.Account;
import models.authorizer.Folder;
import models.authorizer.Organization;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.accountManager.AccountSteps;
import tests.Tests;

@Epic("Финансы")
@Feature("Счета")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class AccountTest extends Tests {
    AccountSteps accountSteps = new AccountSteps();

    @Order(1)
    @Test
    @DisplayName("Создание счета для папки Бизнес блок")
    public void createAccountBusinessBlock() {
        Folder folder = Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject();
        Account.builder().folder(folder).build().createObject();
    }

    @Test
    @Order(2)
    @DisplayName("Создание счета для папки Департамент")
    public void createAccountDepartment() {
        Folder folder = Folder.builder().kind(Folder.DEPARTMENT).build().createObject();
        Account.builder().folder(folder).build().createObject();
    }

    @Test
    @Order(3)
    @DisplayName("Создание счета для папки")
    public void createAccount() {
        Folder folder = Folder.builder().kind(Folder.DEFAULT).build().createObject();
        Account.builder().folder(folder).build().createObject();
    }

    @Test
    @Order(4)
    @DisplayName("Перевод средств между организацией и любой другой папкой")
    void transferMoneyFromAccountOrganizationToBusinessBlock() {
        Organization organization = Organization.builder().build().createObject();
        String accountFrom = accountSteps.getAccountIdByContext(organization.getName());
        Folder folderTo = Folder.builder().build().createObject();
        String accountTo = ((Account) Account.builder().folder(folderTo).build().createObject()).getAccountId();
        accountSteps.transferMoney(accountFrom, accountTo, "10000.00", "Перевод в рамках тестирования");
        accountSteps.transferMoney(accountTo, accountFrom, "10000.00", "Перевод в рамках тестирования");
    }

    @Test
    @Order(5)
    @Deleted
    @DisplayName("Удаление счета для папки")
    public void DeleteAccount() {
        Folder folder = Folder.builder().kind(Folder.DEFAULT).build().createObject();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }

    @Test
    @Order(6)
    @Deleted
    @DisplayName("Удаление счета для папки Department")
    public void DeleteAccountDepartment() {
        Folder folder = Folder.builder().kind(Folder.DEPARTMENT).build().createObject();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }

    @Test
    @Order(7)
    @Deleted
    @DisplayName("Удаление счета для папки Business Block")
    public void DeleteAccountBusinessBlock() {
        Folder folder = Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }
}