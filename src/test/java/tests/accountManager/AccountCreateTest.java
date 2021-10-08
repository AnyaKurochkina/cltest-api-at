package tests.accountManager;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.accountManager.Account;
import models.authorizer.Folder;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Финансы")
@Feature("Счета")
@Tags({@Tag("regress"), @Tag("orgStructure3"), @Tag("smoke")})
public class AccountCreateTest extends Tests {

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

}