package ui.t1.pages.IAM;

import com.codeborne.selenide.Condition;
import ui.elements.Button;
import ui.elements.Dialog;

import static core.helper.StringUtils.$x;

public class ModalWindow {
    private String name;
    Button addRole = Button.byText("Назначить роль");


    public ModalWindow(String name) {
        $x("//span[contains(., '{}')]", name).shouldBe(Condition.visible);
        this.name = name;
    }

    public void setRole() {
        addRole.click();
        new Dialog(name).setInputValue("", "");
    }
}
