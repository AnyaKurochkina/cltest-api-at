package ui.t1.pages.IAM.serviceAccounts;

import com.codeborne.selenide.Condition;
import core.enums.Role;
import io.qameta.allure.Step;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.ServiceAccount;
import org.json.JSONObject;
import ui.cloud.pages.productCatalog.DeleteDialog;
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
    Button delete = Button.byXpath("//span[text() = 'API-ключ']/following::button[@label = 'Удалить']");

    public ServiceAccountPage(String name) {
        $x("//span[text() = '{}']", name).shouldBe(Condition.visible);
    }

    @Step("Проверка заголовков таблицы и табов")
    public ServiceAccountPage checkHeadersAndTabs() {
        List<String> roles = new Table("Роли").getNotEmptyHeaders();
        assertEquals(headers, roles);
        assertTrue(apiKeysTab.isVisible());
        assertTrue(s3KeysTab.isVisible());
        return this;
    }

    @Step("Проверка данных аккаунта")
    public ServiceAccountPage checkAccountDataInServiceAccountPage(ServiceAccount account) {
        GlobalUser user = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
        Table table = new Table("Роли");
        List<String> actualRoles = Arrays.asList(table.getValueByColumnInFirstRow("Роли").getText().split(",\n"));
        List<String> expectedRoles = account.getRoles();
        String userName = table.getValueByColumnInFirstRow("Создатель").getText();
        String id = table.getValueByColumnInFirstRow("Идентификатор").getText();
        account.setId(id);
        assertTrue(id.startsWith("sa_proj-"));
        assertEquals(user.getEmail(), userName);
        assertTrue(actualRoles.containsAll(expectedRoles) && expectedRoles.containsAll(actualRoles));
        return this;
    }

    @Step("Переход на страницу списка Сервисных аккаунтов")
    public ServiceAccountsListPage goToServiceAccountList() {
        Breadcrumb.click("Сервисные аккаунты");
        return new ServiceAccountsListPage();
    }

    @Step("Создание Апи ключа")
    public JSONObject createApiKey(String title) {
        create.click();
        Alert.green("API ключ успешно создан");
        Button.byText("Скопировать данные формы").click();
        Alert.green("Данные успешно скопированы");
        String url = $x("//*[text() = 'Адрес сервиса авторизации:']/following-sibling::div").getText();
        String id = $x("//*[text() = 'Идентификатор:']/following-sibling::div").getText();
        String clientId = $x("//*[text() = 'Ключ:']/following-sibling::div").getText();
        JSONObject jsonObject = new JSONObject()
                .put("url", url)
                .put("name", id)
                .put("title", title)
                .put("secretKey", clientId);
        Button.byText("Подтверждаю, что данные мной сохранены").click();
        return jsonObject;
    }

    @Step("Удаление апи ключа")
    public ServiceAccountPage deleteApiKey() {
        delete.click();
        new DeleteDialog("Потдверждение удаления Api-ключа").clickButton("Да");
        Alert.green("API-ключ успешно удален");
        return this;
    }

    public boolean isTableEmpty() {
        return new Table("Дата добавления").isEmpty();
    }
}
