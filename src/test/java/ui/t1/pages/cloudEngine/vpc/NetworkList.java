package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

import static core.helper.StringUtils.$x;

public class NetworkList {

    @Step("Добавить сеть {name}")
    public void addNetwork(String name, String desc) {
        new NetworksTable().clickAdd();
        Input.byLabel("Имя").setValue(name);
        TextArea.byLabel("Описание").setValue(desc);
        Dialog.byTitle("Добавить сеть").clickButton("Добавить");
        Assertions.assertTrue(new NetworksTable().isColumnValueEquals(Column.NAME, name));
        Waiting.findWidthRefresh(() -> new NetworksTable().getRowByColumnValue(Column.NAME, name).getValueByColumn(Column.STATUS).equals("Доступно"),
                Duration.ofMinutes(1));
    }

    @Step("Удалить сеть {name}")
    public void deleteNetwork(String name) {
        Menu.byElement(new NetworksTable().getRowByColumnValue(Column.NAME, name).get().$("button")).select("Удалить");
        Waiting.findWidthRefresh(() -> !new NetworksTable().isColumnValueEquals(Column.NAME, name), Duration.ofMinutes(1));
    }

    @Step("Выбрать сеть {name}")
    public Network selectNetwork(String name) {
        new NetworksTable().getRowByColumnValue(Column.NAME, name).get().click();
        $x("//span[.='{}']", name).shouldBe(Condition.visible);
        return new Network();
    }

    public static class NetworksTable extends DataTable {
        public NetworksTable() {
            super(Column.NAME);
        }
    }
}
