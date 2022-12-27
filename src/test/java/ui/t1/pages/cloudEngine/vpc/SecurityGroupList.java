package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import ui.elements.DataTable;
import ui.elements.Dialog;
import ui.elements.Input;
import ui.elements.TextArea;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.t1.pages.cloudEngine.vpc.SecurityGroup.RulesTable.COLUMN_STATUS;
import static ui.t1.pages.cloudEngine.vpc.SecurityGroupList.SecurityGroupsTable.getSecurityGroup;

public class SecurityGroupList {

    public void addGroup(String name, String desc) {
        new SecurityGroupsTable().clickAdd();
        Input.byLabel("Имя").setValue(name);
        TextArea.byLabel("Описание").setValue(desc);
        Dialog.byTitle("Добавить группу безопасности").clickButton("Добавить");
        Waiting.findWidthRefresh(() -> getSecurityGroup(name).getValueByColumn(COLUMN_STATUS).equals("Доступно"), Duration.ofMinutes(1));
    }

    public void deleteGroup(String name) {
        getSecurityGroup(name).get().$("button").click();
        Waiting.findWidthRefresh(() -> !new SecurityGroupsTable().isColumnValueEquals(SecurityGroupsTable.COLUMN_NAME, name), Duration.ofMinutes(1));
    }

    public SecurityGroup selectGroup(String name) {
        getSecurityGroup(name).get().click();
        $x("//span[.='{}']", name).shouldBe(Condition.visible);
        return new SecurityGroup();
    }

    public static class SecurityGroupsTable extends DataTable {
        public static final String COLUMN_NAME = "Наименование";

        public SecurityGroupsTable() {
            super(COLUMN_NAME);
        }

        public static Row getSecurityGroup(String name) {
            return new SecurityGroupsTable().getRowByColumnValue(SecurityGroupsTable.COLUMN_NAME, name);
        }
    }
}
