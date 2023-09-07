package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;
import ui.models.StorageProfile;
import ui.t1.pages.IProductT1Page;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DataCentrePage extends IProductT1Page<DataCentrePage> {
    public static final String INFO_DATA_CENTRE = "Информация о Виртуальном дата-центре";
    public static final String PUBLIC_IP_ADDRESSES = "Публичные IP-адреса";
    public static final String ROUTER_INFO = "Информация о маршрутизаторе";

    private final SelenideElement totalRam = $x("//span[text() = 'RAM, ГБ']//preceding-sibling::div//span[2]");
    private final SelenideElement totalCPU = $x("//span[text() = 'CPU, ядра']//preceding-sibling::div//span[2]");

    private final SelenideElement VMwareOrgPage = $x("//*[text() = 'VMware организация']");

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

    public void changeRouterConfig(String speed, String configType) {

        runActionWithParameters(ROUTER_INFO, "Изменить конфигурацию", "Подтвердить", () ->
        {
            Slider.byLabel("Лимит пропускной способности канала, Мбит/сек").setValue(speed);
            RadioGroup.byLabel("Тип конфигурации").select(configType);
            CheckBox.byId("root_high_available").setChecked(true);
        }, ActionParameters.builder()
                .waitChangeStatus(true)
                .timeOut(Duration.ofMinutes(3))
                .build());
        Waiting.sleep(5000);
        generalInformation.click();
        RouterTable routerTable = new RouterTable();
        assertEquals(speed, routerTable.getValueByColumnInFirstRow("Гарантированная ширина канала, Мбит/сек").getText());
        assertEquals(configType, routerTable.getValueByColumnInFirstRow("Конфигурация").getText());
        assertEquals("Да", routerTable.getValueByColumnInFirstRow("High availability").getText());
    }

    public void addProfile(StorageProfile profile) {
        runActionWithParameters(INFO_DATA_CENTRE, "Управление дисковой подсистемой", "Подтвердить", () -> {
            Button.byText("Добавить профиль оборудования").click();
            Select.byXpath("(//button[@title='Open'])[2]").set(profile.getName());
            TextArea.byXPath("//table[thead/tr/th[contains(., 'Профиль оборудования')]]//tr[td][2]//textarea").setValue(profile.getLimit());
            $x("//table[thead/tr/th[contains(., 'Профиль оборудования')]]//tr[td][2]//input[@type = 'radio']")
                    .click();
            Waiting.sleep(5000);
        });
        Waiting.sleep(5000);
        generalInformation.click();
        assertEquals(profile.getLimit(), new StorageProfileTable().getRowByColumnValue("Имя", profile.getName())
                .getValueByColumn("Лимит, Гб"));
        assertEquals("Да", new StorageProfileTable().getRowByColumnValue("Имя", profile.getName())
                .getValueByColumn("Используется по умолчанию"));
    }

    public void deleteProfile(StorageProfile profile) {
        runActionWithParameters(INFO_DATA_CENTRE, "Управление дисковой подсистемой", "Подтвердить", () -> {
            $x("(//table[thead/tr/th[contains(., 'Профиль оборудования')]]//tr[td][2]//button)[3]")
                    .click();
        });
        Waiting.sleep(5000);
        generalInformation.click();
        assertFalse(new StorageProfileTable().isColumnValueContains("Имя", profile.getName()));
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

    @Step("Переход на страницу VMware организация")
    public VMwareOrganizationPage goToVMwareOrgPage() {
        VMwareOrgPage.click();
        return new VMwareOrganizationPage();
    }

    private static class IpTable extends Table {

        public IpTable() {
            super("IP-адрес", 2);
        }
    }

    private static class StorageProfileTable extends Table {

        public StorageProfileTable() {
            super("Лимит, Гб");
        }
    }

    private static class RouterTable extends Table {

        public RouterTable() {
            super("High availability");
        }
    }
}
