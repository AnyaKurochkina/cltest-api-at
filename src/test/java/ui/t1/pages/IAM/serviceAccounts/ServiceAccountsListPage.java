package ui.t1.pages.IAM.serviceAccounts;

import com.codeborne.selenide.Condition;
import models.cloud.authorizer.ServiceAccount;
import ui.elements.*;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.format;

public class ServiceAccountsListPage {

    private final Button createServiceAccountButton = Button.byXpath("//div[@data-testid = 'service-accounts-add-button']//button");

    public ServiceAccountsListPage() {
        $x("//div[text() = 'Сервисные аккаунты' and @type]").shouldBe(Condition.visible);
    }

    public ServiceAccountPage createServiceAccount(ServiceAccount account) {
        String accTitle = account.getTitle();
        createServiceAccountButton.click();
        Dialog createDialog = Dialog.byTitle("Создать сервисный аккаунт");
        createDialog.setInputValueV2("Название", accTitle);
        MultiSelect roles = MultiSelect.byLabel("Роли в каталоге");
        roles.set(account.getRoles());
        createDialog.clickButton("Создать");
        Alert.green(format("Сервисный аккаунт {} успешно создан", accTitle));
        return new ServiceAccountPage(accTitle);
    }

    public ServiceAccountPage editServiceAccount(ServiceAccount account) {
        Menu.byElement(new Table("Название").getRowByColumnValue("Название", account.getTitle())
                .getElementLastColumn())
                .select("Редактировать");

        return null;
    }

    public Boolean isServiceAccountExist(String title) {
        return new Table("Название").isColumnValueEquals("Название", title);
    }
}
