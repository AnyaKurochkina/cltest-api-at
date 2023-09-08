package ui.t1.pages.IAM.serviceAccounts;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.ServiceAccount;
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

    public ServiceAccountPage checkAccountData(ServiceAccount account) {
        GlobalUser user = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
        Table table = new Table("Роли");
        String expectedRoles = table.getValueByColumnInFirstRow("Роли").getText();
        StringBuilder roles = new StringBuilder();
        for (String s : account.getRoles()) {
            roles.append(s).append("\n");
        }
        String actualRoles = roles.toString().trim();
        String userName = table.getValueByColumnInFirstRow("Создатель").getText();
        String id = table.getValueByColumnInFirstRow("Идентификатор").getText();
        assertTrue(id.startsWith("sa_proj-"));
        assertEquals(user.getUsername(), userName);
        assertEquals(expectedRoles, actualRoles);
        return this;
    }

    public ServiceAccountsListPage goToServiceAccountList() {
        Breadcrumb.click("Сервисные аккаунты");
        return new ServiceAccountsListPage();
    }
}
