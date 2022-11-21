package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ui.elements.CheckBox;
import ui.elements.Dialog;
import ui.elements.Table;

import static core.helper.StringUtils.$x;

@Getter
public class SecurityGroupPage {
    final SelenideElement btnAddRule = $x("//button[.='Добавить']");

    public Rule addRole(){
        return new Rule();
    }

    public static class Rule {
        Dialog dlg;
        String description;

        public Rule setDescription(String description) {
            dlg.setInputValue("Описание", description);
            return this;
        }

        public Rule set(boolean checked) {
            dlg.setCheckBox(CheckBox.byLabel("Любой"), checked);
            return this;
        }

        public void clickAdd() {
            dlg.clickButton("Добавить");
            dlg.getDialog().shouldNotBe(Condition.visible);
            Assertions.assertTrue(new SecurityGroupTable().isColumnValueEquals(SecurityGroupTable.COLUMN_DESC, description));
            //TODO: нужна проверка стутуса
        }
    }


    private static class SecurityGroupTable extends Table {
        public static final String COLUMN_DESC = "Описание";

        public SecurityGroupTable() {
            super(COLUMN_DESC);
        }
    }
}
