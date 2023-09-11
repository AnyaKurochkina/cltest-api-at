package ui.t1.pages.IAM.users;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import ui.elements.*;
import ui.models.IamUser;
import ui.t1.tests.IAM.users.AddUserDialog;

import java.util.Arrays;
import java.util.List;

import static api.Tests.activeCnd;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;
import static ui.cloud.pages.productCatalog.EntityListPage.openActionMenu;

@Log4j2
public class UsersPage {

    private final Button addUserBtn = Button.byElement($x("//*[@data-testid='user-management-add-button']//button"));
    private final Button closeDialog = Button.byText("Закрыть");
    private final Button cancelDialog = Button.byText("Отмена");
    private final Button confirmChangesBtn = Button.byText("Применить", 2);
    private static final Button confirmSearchBtn = Button.byText("Применить");
    private final Button openRolesListBtn = Button.byXpath("//button[@title = 'Open']");
    private final Button closeRolesListBtn = Button.byXpath("//button[@title = 'Close']");
    private static final Input searchUser = Input.byPlaceholder("Введите данные пользователя");
    private static final Button clearFilters = Button.byText("Сбросить фильтры");

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
        if (isUserAddedByEmail(user.getEmail())) {
            removeUser(user);
        }
        addUserBtn.click();
        new AddUserDialog(WebDriverRunner.currentFrameUrl()).addUser(user);
        Alert.green("Добавлены пользователи: {}", user.getEmail());
        assertTrue(isUserAdded(user), "Пользователь не найден");
        return this;
    }

    @Step("Отозвать права у пользователя")
    public UsersPage removeUser(IamUser user) {
        openActionMenu("Пользователь", user.getEmail());
        $x("(//*[text()= 'Отозвать права'])[1]").click();
        new Dialog("Подтверждение").clickButton("Отозвать права");
        Alert.green("Удалены все роли у пользователя {}", user.getEmail());
        assertFalse(isUserAdded(user), "Пользователь найден");
        return this;
    }

    @Step("Добавить роли")
    public UsersPage addRole(IamUser user, String roleName) {
        openActionMenu("Пользователь", user.getEmail());
        $x("//*[text()= 'Редактировать']").click();
        openRolesListBtn.click();
        StringUtils.$x("//li[@role = 'menuitem' and text() = 'Базовые']").click();
        StringUtils.$x("//li[@role = 'option']//div[text() = '{}']", roleName).click();
        Waiting.sleep(1000);
        assertTrue(StringUtils.$x("//*[@role = 'button']//*[text() = '{}']", roleName).isDisplayed());
        closeRolesListBtn.click();
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
        closeDialog.click();
        return this;
    }

    @Step("Показать пользователей")
    public UsersPage showUsers(String text) {
        StringUtils.$x("//*[@id='selectValueWrapper']").click();
        StringUtils.$x("//div[text() = '{}']", text).shouldBe(Condition.visible).click();
        return this;
    }

    @Step("Проверка заголовков таблицы")
    public UsersPage checkTableHeaders(List<String> tableHeaders) {
        List<String> notEmptyHeaders = new UsersListTable().getNotEmptyHeaders();
        assertEquals(tableHeaders, notEmptyHeaders);
        return this;
    }

    @Step("Проверка существования пользователя в таблице")
    public static boolean isUserAdded(IamUser user) {
        if (clearFilters.getButton().isDisplayed()) {
            clearFilters.click();
        }
        searchUser.setValue(user.getEmail());
        Waiting.sleep(1000);
        confirmSearchBtn.click();
        UsersListTable table = new UsersListTable();
        if (table.isColumnValueEquals("Пользователь", user.getEmail())) {
            List<String> rolesValue = Arrays.asList(table.getRowByColumnValue("Пользователь", user.getEmail())
                    .getValueByColumn("Роли").split("\n"));
            log.info(rolesValue);
            return isRolesEquals(rolesValue, user.getRole());
        }
        return false;
    }

    @Step("Проверка существования пользователя в таблице только по email")
    public static boolean isUserAddedByEmail(String email) {
        if (clearFilters.getButton().isDisplayed()) {
            clearFilters.click();
        }
        searchUser.setValue(email);
        Waiting.sleep(1000);
        confirmSearchBtn.click();
        UsersListTable table = new UsersListTable();
        return table.isColumnValueEquals("Пользователь", email);
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
