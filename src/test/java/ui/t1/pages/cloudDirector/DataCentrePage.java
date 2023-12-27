package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;
import ui.models.cloudDirector.StorageProfile;
import ui.models.cloudDirector.Vdc;
import ui.t1.pages.IProductT1Page;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.*;

public class DataCentrePage extends IProductT1Page<DataCentrePage> {
    public static final String INFO_DATA_CENTRE = "Информация о Виртуальном дата-центре";
    public static final String ROUTER_INFO = "Маршрутизатор";

    private final SelenideElement totalRam = $x("//span[text() = 'RAM, Гб']//preceding-sibling::div//span[2]");
    private final SelenideElement totalCPU = $x("//span[text() = 'CPU, ядра']//preceding-sibling::div//span[2]");
    private final SelenideElement totalStorage = $x("//span[text() = 'Storage, Гб']//preceding-sibling::div//span[2]");

    private final SelenideElement VMwareOrgPage = $x("//*[text() = 'VMware организация']");

    private final Button generalInformation = Button.byText("Общая информация");

    public void delete() {
        runActionWithParameters(INFO_DATA_CENTRE, "Удалить VDC", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        Waiting.find(() -> new TopInfo().getPowerStatus().equals(TopInfo.POWER_STATUS_DELETED), Duration.ofSeconds(100));
    }

    public void addIpAddresses(int ipQty) {
        runActionWithParameters(ROUTER_INFO, "Зарезервировать внешние IP адреса", "Подтвердить", () ->
                Slider.byLabel("Количество дополнительных внешних IPv4 адресов").setValue(ipQty));
        Waiting.sleep(5000);
        generalInformation.click();
        new RouterTable().getRow(0).get().click();
        new Table("Дополнительные IP адреса").getRow(0).get().click();
        assertEquals(ipQty, new IpTable().getRows().size());
    }

    public void addEdge(String routerBandwidth) {
        runActionWithParameters(INFO_DATA_CENTRE, "Создать маршрутизатор (Edge)", "Подтвердить", () ->
                Select.byLabel("Лимит пропускной способности канала, Мбит/сек").set(routerBandwidth));
        // Waiting.sleep(5000);
        generalInformation.click();
        String value = new RouterTable().getFirstValueByColumn("Пропускная способность, Мбит/сек");
        assertEquals(routerBandwidth, value);
    }

    public void deleteEdge() {
        runActionWithoutParameters(new RouterTable().getRows().first().$x(".//button[@id = 'actions-menu-button']"), "Удалить маршрутизатор");
        // Waiting.sleep(5000);
        generalInformation.click();
        assertTrue(new RouterTable().isEmpty(), "Маршрутизатор не удален");
    }

    public void changeRouterConfig(String speed, String configType) {
        runActionWithParameters(ROUTER_INFO, "Изменить лимит пропускной способности", "Подтвердить", () ->
                        Select.byLabel("Лимит пропускной способности канала, Мбит/сек").set(speed),
                ActionParameters.builder()
                        .waitChangeStatus(true)
                        .timeout(Duration.ofMinutes(3))
                        .build());
        Waiting.sleep(5000);
        generalInformation.click();
        RouterTable routerTable = new RouterTable();
        assertEquals(speed, routerTable.getValueByColumnInFirstRow("Пропускная способность, Мбит/сек").getText());
        assertEquals(configType, routerTable.getValueByColumnInFirstRow("Конфигурация").getText());
    }

    public void addProfile(StorageProfile profile) {
        runActionWithParameters(INFO_DATA_CENTRE, "Управление дисковой подсистемой", "Подтвердить", () -> {
            Table profileTable = new Table($x("//table[thead/tr/th[contains (., 'Профиль оборудования')]]"));
            if (!profileTable.isColumnValueEquals("Профиль оборудования *", profile.getName())) {
                Button.byText("Добавить профиль оборудования").click();
                new Select(profileTable.getRow(1)
                        .getElementByColumn("Профиль оборудования *"))
                        .set(profile.getName());
                TextArea.byName("limit", 2).setValue(profile.getLimit());
                Radio.byName("default", 2).checked();
                Waiting.sleep(5000);
            }
        });
        Waiting.sleep(5000);
        generalInformation.click();
        assertEquals(profile.getLimit(), new StorageProfileTable().getRowByColumnValue("Имя", profile.getName())
                .getValueByColumn("Лимит, Гб"));
        assertEquals("Да", new StorageProfileTable().getRowByColumnValue("Имя", profile.getName())
                .getValueByColumn("Используется по умолчанию"));
    }

    public void deleteProfile(StorageProfile profile) {
        runActionWithParameters(INFO_DATA_CENTRE, "Управление дисковой подсистемой", "Подтвердить", () ->
                $x("(//table[thead/tr/th[contains(., 'Профиль оборудования')]]//tr[td][2]//button)[3]")
                        .click());
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

    @Step("Проверка параметров созданного VDC")
    public void checkVdcParams(Vdc vdc) {
        assertEquals(vdc.getCpu(), totalCPU.getText());
        assertEquals(vdc.getRam(), totalRam.getText());
        assertEquals(vdc.getStorageProfile().getLimit(), totalStorage.getText());
    }

    @Step("Переход на страницу VMware организация")
    public VMwareOrganizationPage goToVMwareOrgPage() {
        VMwareOrgPage.click();
        return new VMwareOrganizationPage();
    }

    private static class StorageProfileTable extends Table {

        public StorageProfileTable() {
            super("Лимит, Гб");
        }
    }

    private static class RouterTable extends Table {

        public RouterTable() {
            super("Пропускная способность, Мбит/сек");
        }
    }

    private static class IpTable extends Table {

        public IpTable() {
            super("IP адрес");
        }
    }
}
