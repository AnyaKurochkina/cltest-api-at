package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.Dialog;
import ui.elements.Slider;
import ui.t1.pages.cloudCompute.Disk;
import ui.t1.pages.cloudCompute.IProductT1Page;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataCentrePage extends IProductT1Page {
    public static final String INFO_DATA_CENTRE = "Информация о Виртуальном дата-центре";
    public static final String PUBLIC_IP_ADDRESSES = "Публичные IP-адреса";

    private final SelenideElement totalRam = $x("//span[text() = 'RAM, ГБ']//preceding-sibling::div//span[2]");
    private final SelenideElement totalCPU = $x("//span[text() = 'CPU, ядра']//preceding-sibling::div//span[2]");

    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(INFO_DATA_CENTRE, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        Waiting.sleep(10000);
        checkPowerStatus(Disk.TopInfo.POWER_STATUS_DELETED);
    }

    public void addIpAddresses(int ipQty) {
        runActionWithParameters(PUBLIC_IP_ADDRESSES, "Зарезервировать внешние IP адреса", "Подтвердить", () ->
                Slider.byLabel("Количество дополнительных внешних IPv4 адресов").setValue(ipQty));
        Waiting.sleep(5000);
        assertEquals(ipQty, new IpTable().getRows().size());
    }

    public void changeConfig(int cpu, int ram) {
        runActionWithParameters(INFO_DATA_CENTRE, "Изменить конфигурацию VDC", "Подтвердить", () ->
        {
            Slider.byLabel("Выделенные ресурсы CPU, Cores").setValue(cpu);
            Slider.byLabel("Выделенные ресурсы MEMORY, Gb").setValue(ram);
        });
        Waiting.sleep(5000);
        Button.byText("Общая информация").click();
        assertEquals(String.valueOf(cpu), totalCPU.getText());
        assertEquals(String.valueOf(ram), totalRam.getText());
    }

    private static class IpTable extends DataTable {
        public static final String COLUMN_NAME = "IP-адрес";

        public IpTable() {
            super(COLUMN_NAME);
        }
    }
}
