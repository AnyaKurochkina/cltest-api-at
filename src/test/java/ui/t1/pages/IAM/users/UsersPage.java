package ui.t1.pages.IAM.users;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Table;
import ui.elements.TextArea;
import ui.models.IamUser;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class UsersPage {

    Button addUserBtn = Button.byElement($x("//*[@data-testid='user-management-add-button']//button"));
    Button confirmAddUserBtn = Button.byText("Добавить");

    public UsersPage() {
        SelenideElement selenideElement = $x("//*[text() = 'Пользователи']");
        selenideElement.shouldBe(Condition.visible);
    }

    @Step("Добавление пользователя")
    public UsersPage addUser(IamUser user) {
        addUserBtn.click();
        TextArea.byName("userList").setValue(user.getEmail());
        StringUtils.$x("//*[contains(@id, '{}')]", user.getEmail()).click();
        Input.byLabel("Роли").click();
        StringUtils.$x("//li[@role = 'menuitem' and text() = 'Базовые']").click();
        StringUtils.$x("//li[@role = 'option']//div[text() = '{}']", user.getRole().get(0)).click();
        assertTrue(StringUtils.$x("//*[@role = 'button']//*[text() = '{}']", user.getRole().get(0)).isDisplayed());
        confirmAddUserBtn.click();
        Waiting.sleep(1000);
        return this;
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
