package ui.t1.pages.IAM;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import core.helper.StringUtils;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.Table;
import ui.models.IamUser;

import java.util.Objects;

import static core.helper.StringUtils.$x;

public class ModalWindow {
    private String name;
    Button addRole = Button.byTextContains("Назначить роль");

    public ModalWindow(String name) {
        $x("//span[contains(., '{}')]", name).shouldBe(Condition.visible);
        this.name = name;
    }
    public ModalWindow() {
    }

    public ModalWindow setRole(IamUser user) {
        addRole.click();
        new AddUserDialog(WebDriverRunner.currentFrameUrl()).addUser(user);
        Alert.green(String.format("Добавлены пользователи: %s;", user.getEmail()));
        return this;
    }

    public ModalWindow deleteUser(IamUser user) {
        expandRow(user.getRole().get(0));
        Button.byElement(StringUtils.$x("//*[text() = '{}']/ancestor::td//following-sibling::td//button[@aria-label = 'delete']", user.getEmail()))
                .click();
        Dialog.byTitle("Подтверждение удаления").clickButton("Удалить");
        Alert.green(String.format("Удалена роль %s у пользователя %s", user.getRole().get(0), user.getEmail()));
        return this;
    }

    public Boolean isUserAdded(IamUser user) {
        expandRow(user.getRole().get(0));
        return new AddedUsersTable().isColumnValueEquals("Название", user.getEmail());
    }

    public Boolean isUserTableEmpty() {
        return new AddedUsersTable().isEmpty();
    }

    private void expandRow(String userRole) {
        Button expandRow = Button.byElement($x("//div[text() = '{}']/preceding-sibling::span/button", userRole));
        if (Objects.isNull(expandRow.getButton().getAttribute("aria-expanded"))) {
            expandRow.click();
        }
    }

    private static class AddedUsersTable extends Table {

        public AddedUsersTable() {
            super(Selenide.$x("(//table[thead/tr/td[.='Название']])[2]"));
        }
    }
}
