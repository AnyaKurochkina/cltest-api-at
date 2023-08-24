package ui.t1.pages.IAM;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import ui.elements.Button;
import ui.elements.Table;
import ui.models.IamUser;
import ui.t1.tests.IAM.users.AddUserDialog;

import static core.helper.StringUtils.$x;

public class ModalWindow {
    private String name;
    Button addRole = Button.byText("Назначить роль");

    public ModalWindow(String name) {
        $x("//span[contains(., '{}')]", name).shouldBe(Condition.visible);
        this.name = name;
    }

    public ModalWindow setRole(IamUser user) {
        addRole.click();
        new AddUserDialog(WebDriverRunner.currentFrameUrl()).addUser(user);
        return this;
    }

    public Boolean isUserAdded(IamUser user) {
        Button expandRow = Button.byElement($x("//div[text() = '{}']/preceding-sibling::span/button", user.getRole().get(0)));
        expandRow.click();
        return new AddedUsersTable().isColumnValueEquals("Название", user.getEmail());
    }

    private static class AddedUsersTable extends Table {

        public AddedUsersTable() {
            super(Selenide.$x("(//table[thead/tr/td[.='Название']])[2]"));
        }
    }
}
