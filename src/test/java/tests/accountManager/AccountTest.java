package tests.accountManager;

import core.helper.Deleted;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.accountManager.Account;
import models.authorizer.Folder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Финансы")
@Feature("Счета")
@Tags({@Tag("regress"), @Tag("orgStructure3"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class AccountTest extends Tests {

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

//    @Test
//    @Order(4)
//    @DisplayName("Перевод денег со счета организации на счет папки Бизнес блок")
//    public void transferMoneyFromAccountToBusinessBlock() {
//        accountCreate.transferMoneyFromOrganizationToFolder("ВТБ", "BUSINESS_FOLDER", "10000.00");
//    }
//
//    @Test
//    @Order(5)
//    @DisplayName("Перевод денег со счета папки Бизнес блок на счет папки Департамент")
//    public void transferMoneyFromAccountToDepartment() {
//        accountCreate.transferMoneyFromFolderToFolder("BUSINESS_FOLDER", "DEPARTMENT_FOLDER", "10000.00");
//    }
//
//    @Test
//    @Order(6)
//    @Description("Перевод денег со счета папки Департамент на счет папки")
//    public void transferMoneyFromAccountToFolder() {
//        accountCreate.transferMoneyFromFolderToFolder("DEPARTMENT_FOLDER","FOLDER", "10000.00");
//    }

    @Test
    @Order(7)
    @Deleted(Account.class)
    @DisplayName("Удаление счета для папки")
    public void DeleteAccount() {
        Folder folder = Folder.builder().kind(Folder.DEFAULT).build().createObject();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }

    @Test
    @Order(8)
    @Deleted(Account.class)
    @DisplayName("Удаление счета для папки Department")
    public void DeleteAccountDepartment() {
        Folder folder = Folder.builder().kind(Folder.DEPARTMENT).build().createObject();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }

    @Test
    @Order(9)
    @Deleted(Account.class)
    @DisplayName("Удаление счета для папки Business block")
    public void DeleteAccountBusinessBlock() {
        Folder folder = Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject();
        Account.builder().folder(folder).build().createObject().deleteObject();
    }
}