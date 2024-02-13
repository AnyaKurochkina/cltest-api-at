package ui.t1.pages.IAM.serviceAccounts;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Step;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.ServiceAccount;
import org.apache.commons.lang3.RandomStringUtils;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.*;

import java.util.Arrays;
import java.util.List;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.format;
import static core.utils.AssertUtils.assertEqualsList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceAccountsListPage {

    private final Button createServiceAccountButton = Button.byXpath("//div[@data-testid = 'service-accounts-add-button']//button");
    MultiSelect roles = MultiSelect.byLabel("Роли в каталоге");
    Table serviceAccountsTable = new Table("Название");

    public ServiceAccountsListPage() {
        $x("//div[text() = 'Сервисные аккаунты' and @type]").shouldBe(Condition.visible);
    }

    @Step("Создание сервисного аккаунта")
    public ServiceAccountPage createServiceAccount(ServiceAccount account) {
        String accTitle = account.getTitle();
        createServiceAccountButton.click();
        Dialog createDialog = Dialog.byTitle("Создать сервисный аккаунт");
        createDialog.setInputValueV2("Название", accTitle);
        roles.set(account.getRoles().toArray(new String[0]));
        createDialog.clickButton("Создать");
        Alert.green(format("Сервисный аккаунт {} успешно создан", accTitle));
        return new ServiceAccountPage(accTitle);
    }

    @Step("Редактирование сервисного аккаунта")
    public ServiceAccountsListPage editServiceAccount(String title, ServiceAccount account) {
        Menu.byElement(serviceAccountsTable.getRowByColumnValue("Название", title)
                        .getElementLastColumn())
                .select("Редактировать");
        Dialog editDialog = Dialog.byTitle("Редактировать сервисный аккаунт");
        editDialog.setInputValueV2("Название", account.getTitle());
        roles.clear();
        roles.set(account.getRoles().toArray(new String[0]));
        editDialog.clickButton("Применить");
        Alert.green(format("Сервисный аккаунт {} успешно изменен", account.getTitle()));
        return this;
    }

    @Step("Удаление сервисного аккаунта с апи токеном")
    public ServiceAccountsListPage deleteServiceAccountWithApiToken(ServiceAccount account) {
        Menu.byElement(serviceAccountsTable.getRowByColumnValue("Название", account.getTitle())
                        .getElementLastColumn())
                .select("Удалить");
        DeleteDialog deleteDialog = new DeleteDialog("Удаление сервисного аккаунта");
        deleteDialog.inputIdAndCheckNotDeletable("Для удаления сервисного аккаунта необходимо удалить привязанный API-ключ");
        return this;
    }

    @Step("Удаление сервисного аккаунта")
    public ServiceAccountsListPage deleteServiceAccount(ServiceAccount account) {
        Menu.byElement(serviceAccountsTable.getRowByColumnValue("Название", account.getTitle())
                        .getElementLastColumn())
                .select("Удалить");
        DeleteDialog deleteDialog = new DeleteDialog("Удаление сервисного аккаунта");
        deleteDialog.inputInvalidId(RandomStringUtils.randomAlphabetic(6));
        deleteDialog.inputValidIdAndDelete(format("Сервисный аккаунт {} успешно удален", account.getId()));
        return this;
    }

    @Step("Проверка данных аккаунта")
    public ServiceAccountsListPage checkAccountData(ServiceAccount account) {
        GlobalUser user = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
        Table.Row row = serviceAccountsTable.update().getRowByColumnValue("Название", account.getTitle());
        String actualTitle = row.getValueByColumn("Название");
        List<String> actualRoles = Arrays.asList(row.getValueByColumn("Роли").split(",\n"));
        List<String> expectedRoles = account.getRoles();
        String actualUserName = row.getValueByColumn("Создатель");
        String actualId = row.getValueByColumn("Идентификатор");
        assertTrue(actualId.startsWith("sa_proj-"));
        assertEquals(user.getEmail(), actualUserName);
        assertEquals(account.getTitle(), actualTitle);
        assertEqualsList(expectedRoles, actualRoles);;
        return this;
    }

    @Step("Переход в сервисный аккаунт")
    public ServiceAccountPage goToServiceAccountPage(ServiceAccount account) {
        serviceAccountsTable.getRowByColumnValue("Название", account.getTitle()).get().click();
        return new ServiceAccountPage(account.getTitle());
    }

    @Step("Проверка существования сервисного аккаунта {title}")
    public Boolean isServiceAccountExist(String title) {
        return new Table("Название").isColumnValueEquals("Название", title);
    }
}
