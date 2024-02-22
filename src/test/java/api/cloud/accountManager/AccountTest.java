package api.cloud.accountManager;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.accountManager.Account;
import models.cloud.authorizer.Folder;
import models.cloud.authorizer.Organization;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.accountManager.AccountSteps;

@Epic("Финансы")
@Feature("Счета")
@Tags({@Tag("regress"), @Tag("accounts"), @Tag("smoke"), @Tag("prod"), @Tag("health_check")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountTest extends Tests {

    @Order(1)
    @Test
    @DisabledIfEnv("prod")
    @TmsLink("646907")
    @DisplayName("Создание счета для папки Бизнес блок")
    public void createAccountBusinessBlock() {
        Folder folder = Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject();
        Account.builder().folder(folder).build().createObject();
    }

    @Test
    @Order(2)
    @DisabledIfEnv("prod")
    @TmsLink("646958")
    @DisplayName("Создание счета для папки Департамент")
    public void createAccountDepartment() {
        Folder folder = Folder.builder().kind(Folder.DEPARTMENT).build().createObject();
        Account.builder().folder(folder).build().createObject();
    }

    @Test
    @Order(3)
    @TmsLink("646960")
    @DisplayName("Создание счета для папки")
    public void createAccount() {
        Folder folder = Folder.builder().kind(Folder.DEFAULT).build().createObject();
        Account.builder().folder(folder).build().createObject();
    }

    @Test
    @Order(4)
    @DisabledIfEnv("prod")
    @TmsLink("377444")
    @DisplayName("Перевод средств между организацией и любой другой папкой")
    void transferMoneyFromAccountOrganizationToBusinessBlock() {
        Organization organization = Organization.builder().type("default").build().createObject();
        String accountFromId = AccountSteps.getAccountIdByContext(organization.getName());
        Account accountFrom = Account.builder().accountId(accountFromId).folderId(organization.getName()).build();
        Folder folderTo = Folder.builder().kind(Folder.DEFAULT).build().createObject();
        Account accountTo = Account.builder().folder(folderTo).build().createObject();
        AccountSteps.transferMoneyOrganization(accountFrom, accountTo.getAccountId(), "10000.00", "Перевод в рамках тестирования");
        AccountSteps.transferMoney(accountTo, accountFrom.getAccountId(), "10000.00", "Перевод в рамках тестирования");
    }

    @Test
    @Order(5)
    @TmsLink("646965")
    @DisplayName("Удаление счета для папки")
    public void DeleteAccount() {
        Folder folder = Folder.builder().kind(Folder.DEFAULT).build().createObjectPrivateAccess();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }

    @Test
    @Order(6)
    @DisabledIfEnv("prod")
    @TmsLink("646966")
    @DisplayName("Удаление счета для папки Department")
    public void DeleteAccountDepartment() {
        Folder folder = Folder.builder().kind(Folder.DEPARTMENT).build().createObjectPrivateAccess();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }

    @Test
    @Order(7)
    @DisabledIfEnv("prod")
    @TmsLink("646968")
    @DisplayName("Удаление счета для папки Business Block")
    public void DeleteAccountBusinessBlock() {
        Folder folder = Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObjectPrivateAccess();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }
}