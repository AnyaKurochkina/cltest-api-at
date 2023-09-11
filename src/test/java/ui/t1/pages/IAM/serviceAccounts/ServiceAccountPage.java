package ui.t1.pages.IAM.serviceAccounts;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import core.enums.Role;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.ServiceAccount;
import ui.elements.Alert;
import ui.elements.Breadcrumb;
import ui.elements.Button;
import ui.elements.Table;

import java.util.Arrays;
import java.util.List;

import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceAccountPage {

    List<String> headers = Arrays.asList("Роли", "Дата создания", "Создатель", "Идентификатор");
    Button apiKeysTab = Button.byId("api_keys");
    Button s3KeysTab = Button.byId("ceph_public_keys");
    Button create = Button.byText("Создать");

    public ServiceAccountPage(String name) {
        $x("//span[text() = '{}']", name).shouldBe(Condition.visible);
    }

    public ServiceAccountPage checkHeadersAndTabs() {
        List<String> roles = new Table("Роли").getNotEmptyHeaders();
        assertEquals(headers, roles);
        assertTrue(apiKeysTab.isVisible());
        assertTrue(s3KeysTab.isVisible());
        return this;
    }

    public ServiceAccountPage checkAccountDataInServiceAccountPage(ServiceAccount account) {
        GlobalUser user = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
        Table table = new Table("Роли");
        List<String> actualRoles = Arrays.asList(table.getValueByColumnInFirstRow("Роли").getText().split(",\n"));
        List<String> expectedRoles = account.getRoles();
        String userName = table.getValueByColumnInFirstRow("Создатель").getText();
        String id = table.getValueByColumnInFirstRow("Идентификатор").getText();
        assertTrue(id.startsWith("sa_proj-"));
        assertEquals(user.getEmail(), userName);
        assertTrue(actualRoles.containsAll(expectedRoles) && expectedRoles.containsAll(actualRoles));
        return this;
    }

    public ServiceAccountsListPage goToServiceAccountList() {
        Breadcrumb.click("Сервисные аккаунты");
        return new ServiceAccountsListPage();
    }

    public ServiceAccountPage createApiKey() {
        create.click();
        Alert.green("API ключ успешно создан");
        Button.byText("Скопировать данные формы").click();
        Alert.green("Данные успешно скопированы");
        Clipboard clipboard = Selenide.clipboard();
        String foo = clipboard.getText();
        Button.byText("Подтверждаю, что данные мной сохранены").click();
        return this;
    }
}
