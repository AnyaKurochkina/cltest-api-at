package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import ui.elements.DataTable;
import ui.elements.Dialog;
import ui.elements.Table;

import static core.helper.StringUtils.$x;

public class SecurityGroupList {

    public void addGroup(String name, String desc) {
        new SecurityGroupsTable().clickAdd();
        Dialog.byTitle("Добавить группу безопасности")
                .setInputValue("Имя", name)
                .setInputValue("Описание", desc)
                .clickButton("Добавить");
        Assertions.assertTrue(new SecurityGroupsTable().isColumnValueEquals(SecurityGroupsTable.COLUMN_NAME, name));
        //TODO: нужна проверка стутуса
    }

    public void deleteGroup(String name) {
        new SecurityGroupsTable().getRowByColumnValue(SecurityGroupsTable.COLUMN_NAME, name).getElementByColumn("").$("button").click();
        //TODO: нужна проверка отсутствия группы
    }

    public void selectGroup(String name) {
        new SecurityGroupsTable().getRowByColumnValue(SecurityGroupsTable.COLUMN_NAME, name).get().click();
        $x("//span[.='{}']", name).shouldBe(Condition.visible);
    }

    private static class SecurityGroupsTable extends DataTable {
        public static final String COLUMN_NAME = "Наименование";

        public SecurityGroupsTable() {
            super(COLUMN_NAME);
        }
    }
}
