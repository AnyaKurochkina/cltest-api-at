package ui.t1.tests.IAM.users;

import core.helper.StringUtils;
import core.utils.Waiting;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.TextArea;
import ui.models.IamUser;

import java.util.Objects;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.findByRegex;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.authorizer.AuthorizerSteps.getContextName;

public class AddUserDialog extends Dialog {

    private final Button openRolesListBtn = Button.byXpath("//button[@title = 'Open']");
    private final Button closeRolesListBtn = Button.byXpath("//button[@title = 'Close']");
    private final Button confirmAddUserBtn = Button.byText("Добавить");

    public AddUserDialog(String url) {
        StringBuilder title = new StringBuilder();
        String contextType = Objects.requireNonNull(findByRegex("type=(\\w+)", url));
        if (contextType.equals("project")) {
            title.append("Проект").append("  ");
        }
        if (contextType.equals("folder")) {
            title.append("Папка").append("  ");
        }
        if (contextType.equals("organization")) {
            title.append("Организация").append("  ");
        }
        String name = getContextName(Objects.requireNonNull(findByRegex("context=([^&]+)", url)));
        String dialogTitle = title.append(String.format("\"%s\"", name)).toString();
        dialog = $x(xpath, dialogTitle);
    }

    public void addUser(IamUser user) {
        setTextareaAndPressEnter(TextArea.byName("userList"), user.getEmail());
        openRolesListBtn.click();
        StringUtils.$x("//li[@role = 'menuitem' and text() = 'Базовые']").click();
        StringUtils.$x("//li[@role = 'option']//div[text() = '{}']", user.getRole().get(0)).click();
        assertTrue(StringUtils.$x("//*[@role = 'button']//*[text() = '{}']", user.getRole().get(0)).isDisplayed());
        closeRolesListBtn.click();
        confirmAddUserBtn.click();
        Waiting.sleep(1000);
    }
}
