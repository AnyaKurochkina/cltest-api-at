package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.t1.pages.cloudEngine.vpc.NetworkList.NetworksTable.COLUMN_STATUS;

public class NetworkList {

    public void addNetwork(String name, String desc) {
        new NetworksTable().clickAdd();
        Input.byLabel("Имя").setValue(name);
        TextArea.byLabel("Описание").setValue(desc);
        Dialog.byTitle("Добавить сеть").clickButton("Добавить");
        Assertions.assertTrue(new NetworksTable().isColumnValueEquals(NetworksTable.COLUMN_NAME, name));
        Waiting.findWidthRefresh(() -> new NetworksTable().getRowByColumnValue(NetworksTable.COLUMN_NAME, name).getValueByColumn(COLUMN_STATUS).equals("Доступно"),
                Duration.ofMinutes(1));
    }

    public void deleteNetwork(String name) {
        Menu.byElement(new NetworksTable().getRowByColumnValue(NetworksTable.COLUMN_NAME, name).get().$("button")).select("Удалить");
        Waiting.findWidthRefresh(() -> !new NetworksTable().isColumnValueEquals(NetworksTable.COLUMN_NAME, name), Duration.ofMinutes(1));
    }

    public Network selectNetwork(String name) {
        new NetworksTable().getRowByColumnValue(NetworksTable.COLUMN_NAME, name).get().click();
        $x("//span[.='{}']", name).shouldBe(Condition.visible);
        return new Network();
    }

    public static class NetworksTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";
        public static final String COLUMN_STATUS = "Статус";

        public NetworksTable() {
            super(COLUMN_NAME);
        }
    }
}
