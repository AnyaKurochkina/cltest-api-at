package ui.t1.pages.IAM;

import core.utils.Waiting;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.SearchSelect;
import ui.models.IamUser;

import java.util.List;
import java.util.Objects;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.findByRegex;
import static steps.authorizer.AuthorizerSteps.getContextName;

public class AddUserDialog extends Dialog {

    private final Button confirmAddUserBtn = Button.byText("Добавить");
    private final Button applyBtn = Button.byText("Применить", 2);
    private final SearchSelect selectUser = SearchSelect.byLabel("Пользователь");
    private final SearchSelect selectRole = SearchSelect.byLabel("Роли", 2);

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
        selectUser.set(user.getEmail());
        selectUser.close();
        for (String role : user.getRole()) {
            selectRole.set(role);
        }
        selectRole.close();
        confirmAddUserBtn.click();
        Waiting.sleep(1000);
    }

    public void addRole(List<String> roles) {
        for (String role : roles) {
            selectRole.add(role);
        }
        selectRole.close();
        applyBtn.click();
        Waiting.sleep(1000);
    }

    public void removeRole(List<String> roles) {
        for (String role : roles) {
            $x("//div[text() = '{}']/parent::div/button", role).click();
        }
        applyBtn.click();
        Waiting.sleep(1000);
    }
}
