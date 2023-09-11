package ui.t1.pages.IAM.serviceAccounts;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.ServiceAccount;
import ui.elements.*;

import java.util.Arrays;
import java.util.List;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceAccountsListPage {

    private final Button createServiceAccountButton = Button.byXpath("//div[@data-testid = 'service-accounts-add-button']//button");
    MultiSelect roles = MultiSelect.byLabel("Роли в каталоге");

    public ServiceAccountsListPage() {
        $x("//div[text() = 'Сервисные аккаунты' and @type]").shouldBe(Condition.visible);
    }

    public ServiceAccountPage createServiceAccount(ServiceAccount account) {
        String accTitle = account.getTitle();
        createServiceAccountButton.click();
        Dialog createDialog = Dialog.byTitle("Создать сервисный аккаунт");
        createDialog.setInputValueV2("Название", accTitle);
        roles.set(account.getRoles());
        createDialog.clickButton("Создать");
        Alert.green(format("Сервисный аккаунт {} успешно создан", accTitle));
        return new ServiceAccountPage(accTitle);
    }

    public ServiceAccountsListPage editServiceAccount(String title, ServiceAccount account) {
        Menu.byElement(new Table("Название").getRowByColumnValue("Название", title)
                        .getElementLastColumn())
                .select("Редактировать");
        Dialog editDialog = Dialog.byTitle("Редактировать сервисный аккаунт");
        editDialog.setInputValueV2("Название", account.getTitle());
        roles.clear();
        roles.set(account.getRoles());
        editDialog.clickButton("Применить");
        Alert.green(format("Сервисный аккаунт {} успешно изменен", account.getTitle()));
        return this;
    }

    public ServiceAccountsListPage checkAccountData(ServiceAccount account) {
        GlobalUser user = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
        Table table = new Table("Название");
        Table.Row row = table.getRowByColumnValue("Название", account.getTitle());
        String actualTitle = row.getValueByColumn("Название");
        List<String> actualRoles = Arrays.asList(row.getValueByColumn("Роли").split(",\n"));
        List<String> expectedRoles = account.getRoles();
        String actualUserName = row.getValueByColumn("Создатель");
        String actualId = row.getValueByColumn("Идентификатор");
        assertTrue(actualId.startsWith("sa_proj-"));
        assertEquals(user.getEmail(), actualUserName);
        assertEquals(account.getTitle(), actualTitle);
        assertTrue(actualRoles.containsAll(expectedRoles) && expectedRoles.containsAll(actualRoles));
        return this;
    }

    public Boolean isServiceAccountExist(String title) {
        return new Table("Название").isColumnValueEquals("Название", title);
    }
}
