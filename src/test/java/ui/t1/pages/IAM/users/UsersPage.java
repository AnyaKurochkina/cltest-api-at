package ui.t1.pages.IAM.users;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import ui.elements.*;
import ui.models.IamUser;

import java.util.Arrays;
import java.util.List;

import static api.Tests.activeCnd;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ui.cloud.pages.productCatalog.BaseListPage.openActionMenu;

@Log4j2
public class UsersPage {

    Button addUserBtn = Button.byElement($x("//*[@data-testid='user-management-add-button']//button"));
    Button confirmAddUserBtn = Button.byText("Добавить");
    Button confirmChangesBtn = Button.byText("Применить", 2);
    Button openRolesListBtn = Button.byXpath("//button[@title = 'Open']");
    SelenideElement currentAndLowerLevel = StringUtils.$x("//li[@data-value = 'lowerLevel']");

    public UsersPage() {
        SelenideElement selenideElement = $x("//*[text() = 'Пользователи']");
        selenideElement.shouldBe(Condition.visible);
    }

    public UsersPage changeContext(String contextType, String contextValue) {
        open(String.format("/management/users?context=%s&type=%s", contextValue, contextType));
        addUserBtn.getButton().shouldBe(activeCnd);
        return this;
    }

    public UsersPage changeContext(String contextType, String contextValue, String contextName) {
        open(String.format("/management/users?context=%s&type=%s", contextValue, contextType));
        addUserBtn.getButton().shouldBe(activeCnd);
        StringUtils.$x("//div[text() = '{}']", contextName).shouldBe(Condition.visible);
        return this;
    }

    @Step("Добавление пользователя")
    public UsersPage addUser(IamUser user) {
        addUserBtn.click();
        TextArea.byName("userList").setValue(user.getEmail());
        StringUtils.$x("//*[contains(@id, '{}')]", user.getEmail()).click();
        openRolesListBtn.click();
        StringUtils.$x("//li[@role = 'menuitem' and text() = 'Базовые']").click();
        StringUtils.$x("//li[@role = 'option']//div[text() = '{}']", user.getRole().get(0)).click();
        assertTrue(StringUtils.$x("//*[@role = 'button']//*[text() = '{}']", user.getRole().get(0)).isDisplayed());
        confirmAddUserBtn.click();
        Waiting.sleep(1000);
        return this;
    }

    @Step("Отозвать права у пользователя")
    public UsersPage removeUser(IamUser user) {
        openActionMenu("Пользователь", user.getEmail());
        $x("(//*[text()= 'Отозвать права'])[1]").click();
        new Dialog("Подтверждение").clickButton("Отозвать права");
        Alert.green("Удалены все роли у пользователя {}", user.getEmail());
        return this;
    }

    @Step("Добавить роли")
    public UsersPage addRole(IamUser user, String roleName) {
        openActionMenu("Пользователь", user.getEmail());
        $x("//*[text()= 'Редактировать']").click();
        openRolesListBtn.click();
        StringUtils.$x("//li[@role = 'menuitem' and text() = 'Базовые']").click();
        StringUtils.$x("//li[@role = 'option']//div[text() = '{}']", roleName).click();
        assertTrue(StringUtils.$x("//*[@role = 'button']//*[text() = '{}']", roleName).isDisplayed());
        confirmChangesBtn.click();
        Alert.green("Изменен пользователь {}", user.getEmail());
        return this;
    }

    @Step("Удалить роли")
    public UsersPage removeRoles(IamUser user, List<String> roleNames) {
        openActionMenu("Пользователь", user.getEmail());
        $x("//*[text()= 'Редактировать']").click();
        for (String role : roleNames) {
            StringUtils.$x("//*[@role = 'button']//*[text() = '{}']//parent::div/*[local-name() = 'svg']", role).click();
        }
        $x("//*[text() = 'Поле обязательно для заполнения']").shouldBe(Condition.visible);
        confirmChangesBtn.getButton().shouldBe(Condition.disabled);
        return this;
    }

    @Step("Показать пользователей")
    public UsersPage showUsers(String text) {
       // StringUtils.$x("//div[@aria-labelledby = 'ancestors']//parent::div/*[local-name() = 'svg']").click();
        StringUtils.$x("//div[@aria-labelledby = 'ancestors']").click();
        StringUtils.$x("//li[text() = '{}']//ancestor::li[@data-value = 'lowerLevel']", text).click();
        return this;
    }

    @Step("Проверка заголовков таблицы")
    public void checkTableHeaders(List<String> tableHeaders) {
        List<String> notEmptyHeaders = new UsersListTable().getNotEmptyHeaders();
        assertEquals(tableHeaders, notEmptyHeaders);
    }

    @Step("Проверка существования пользователя в таблице")
    public static boolean isUserAdded(IamUser user) {
        UsersListTable table = new UsersListTable();
        if (table.isColumnValueEquals("Пользователь", user.getEmail())) {
            List<String> rolesValue = Arrays.asList(table.getRowByColumnValue("Пользователь", user.getEmail())
                    .getValueByColumn("Роли").split("\n"));
            log.info(rolesValue);
            return isRolesEquals(rolesValue, user.getRole());
        }
        return false;
    }

    private static boolean isRolesEquals(List<String> roles, List<String> userRoles) {
        return roles.size() == userRoles.size() && roles.containsAll(userRoles) && userRoles.containsAll(roles);
    }

    private static class UsersListTable extends Table {

        public UsersListTable() {
            super("Пользователь");
        }
    }
}
