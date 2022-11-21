package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
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
        Dialog dlg = Dialog.byTitle("Добавить правило");

        public Rule set(boolean checked) {
            dlg.setCheckBox(CheckBox.byLabel("Любой"), checked);
            return this;
        }

        public void clickAdd() {
            dlg.clickButton("Добавить");
        }
    }


    private static class SecurityGroupTable extends Table {
        public static final String COLUMN_NAME = "Наименование";

        public SecurityGroupTable() {
            super(COLUMN_NAME);
        }
    }
}
