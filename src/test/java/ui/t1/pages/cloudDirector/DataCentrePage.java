package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.Slider;
import ui.elements.Table;
import ui.t1.pages.IProductT1Page;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataCentrePage extends IProductT1Page<DataCentrePage> {
    public static final String INFO_DATA_CENTRE = "Информация о Виртуальном дата-центре";
    public static final String PUBLIC_IP_ADDRESSES = "Публичные IP-адреса";

    private final SelenideElement totalRam = $x("//span[text() = 'RAM, ГБ']//preceding-sibling::div//span[2]");
    private final SelenideElement totalCPU = $x("//span[text() = 'CPU, ядра']//preceding-sibling::div//span[2]");

    private final Button generalInformation = Button.byText("Общая информация");

    public void delete() {
        runActionWithParameters(INFO_DATA_CENTRE, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        Waiting.find(() -> new TopInfo().getPowerStatus().equals(TopInfo.POWER_STATUS_DELETED), Duration.ofSeconds(100));
    }

    public void addIpAddresses(int ipQty) {
        runActionWithParameters(PUBLIC_IP_ADDRESSES, "Зарезервировать внешние IP адреса", "Подтвердить", () ->
                Slider.byLabel("Количество дополнительных внешних IPv4 адресов").setValue(ipQty));
        Waiting.sleep(5000);
        generalInformation.click();
        assertEquals(ipQty, new IpTable().getRows().size());
    }
    @Step("Освобождение IP адресов")
    public void removeIpAddresses() {
        int count = new IpTable().getRows().size();
        while (count > 0) {
            runActionWithoutParameters(new IpTable().getRows().first().$x(".//button"), "Освободить");
            Waiting.sleep(1000);
            generalInformation.click();
            count--;
        }
    }

    public void changeConfig(int cpu, int ram) {
        runActionWithParameters(INFO_DATA_CENTRE, "Изменить конфигурацию VDC", "Подтвердить", () ->
        {
            Slider.byLabel("Выделенные ресурсы CPU, Cores").setValue(cpu);
            Slider.byLabel("Выделенные ресурсы MEMORY, Gb").setValue(ram);
        });
        Waiting.sleep(5000);
        generalInformation.click();
        assertEquals(String.valueOf(cpu), totalCPU.getText());
        assertEquals(String.valueOf(ram), totalRam.getText());
    }

    private static class IpTable extends Table {
        public static SelenideElement tableIp = $x("(//table[thead/tr/th[.='IP-адрес']])[2]");

        public IpTable() {
            super(tableIp);
        }
    }
}
