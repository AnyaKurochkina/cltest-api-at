package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import models.AbstractEntity;
import ui.elements.DataTable;
import ui.elements.Dialog;
import ui.elements.Input;
import ui.elements.TextArea;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.tests.engine.AbstractComputeTest;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.t1.pages.cloudEngine.vpc.SecurityGroupList.SecurityGroupsTable.getSecurityGroup;

public class SecurityGroupList {

    public SecurityGroupList addGroup(String name, String desc) {
        new SecurityGroupsTable().clickAdd();
        Input.byLabel("Имя").setValue(name);
        TextArea.byLabel("Описание").setValue(desc);
        Dialog.byTitle("Добавить группу безопасности").clickButton("Добавить");
        Waiting.findWithRefresh(() -> {
            SecurityGroupsTable table = new SecurityGroupsTable();
            if(table.isColumnValueEquals(Column.NOMINATION, name))
                return getSecurityGroup(name).getValueByColumn(Column.STATUS).equals("Доступно");
            else return false;
        }, Duration.ofMinutes(1));
        selectGroup(name);
        return this;
    }

    public void markForDeletion(){
        AbstractEntity.addEntity(new AbstractComputeTest.SecurityGroupEntity());
    }

    public void deleteGroup(String name) {
        getSecurityGroup(name).get().$("button").click();
        Waiting.findWithRefresh(() -> !new SecurityGroupsTable().isColumnValueEquals(Column.NOMINATION, name), Duration.ofMinutes(1));
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
