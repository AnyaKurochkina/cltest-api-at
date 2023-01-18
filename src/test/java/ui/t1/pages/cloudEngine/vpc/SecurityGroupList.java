package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.t1.pages.cloudEngine.vpc.SecurityGroupList.SecurityGroupsTable.getSecurityGroup;

public class SecurityGroupList {

    public void addGroup(String name, String desc) {
        new SecurityGroupsTable().clickAdd();
        Input.byLabel("Имя").setValue(name);
        TextArea.byLabel("Описание").setValue(desc);
        Dialog.byTitle("Добавить группу безопасности").clickButton("Добавить");
        Waiting.findWidthRefresh(() -> getSecurityGroup(name).getValueByColumn(Column.STATUS).equals("Доступно"), Duration.ofMinutes(1));
    }

    public void deleteGroup(String name) {
        getSecurityGroup(name).get().$("button").click();
        Waiting.findWidthRefresh(() -> !new SecurityGroupsTable().isColumnValueEquals(Column.NOMINATION, name), Duration.ofMinutes(1));
    }

    public SecurityGroup selectGroup(String name) {
        getSecurityGroup(name).get().click();
        $x("//span[.='{}']", name).shouldBe(Condition.visible);
        return new SecurityGroup();
    }

    public static class SecurityGroupsTable extends DataTable {

        public SecurityGroupsTable() {
            super(Column.NOMINATION);
        }

        public static Row getSecurityGroup(String name) {
            return new SecurityGroupsTable().getRowByColumnValue(Column.NOMINATION, name);
        }
    }
}
