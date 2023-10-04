package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.elements.MuiGridItem;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.Dialog;
import ui.elements.Select;
import ui.elements.Table;

@Getter
public class Vm extends IProductT1Page<Vm> {
    private final MuiGridItem osElement = MuiGridItem.byText("Операционная система");
    private final MuiGridItem descriptionElement = MuiGridItem.byText("Описание");
    private final MuiGridItem availabilityZoneElement = MuiGridItem.byText("Зона доступности");
    private final MuiGridItem nameElement = MuiGridItem.byText("Имя");

    public Disk selectDisk(String disk) {
        new Disk.DiskInfo().getRowByColumnValue(Column.NAME, disk).getElementByColumn(Column.NAME).shouldBe(Condition.visible).click();
        return new Disk();
    }

    @Step("Подключить {ip}")
    public void attachIp(String ip) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить публичный IP", "Подтвердить", () ->
                Dialog.byTitle("Подключить публичный IP")
                        .setSelectValue("Сетевой интерфейс", Select.RANDOM_VALUE)
                        .setSelectValue("Публичный IP", ip));
    }

    @Step("Остановить ВМ")
    public void stop() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Остановить");
        checkPowerStatus(VirtualMachine.POWER_STATUS_OFF);
    }

    @Step("Запустить ВМ")
    public void start() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Запустить");
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }

    @Step("Изменить конфигурацию ВМ")
    public void resize(String flavorName) {
        runActionWithParameters(BLOCK_PARAMETERS, "Изменить конфигурацию", "Подтвердить",
                () -> new VmCreate().setFlavorName(flavorName));
    }

    public NetworkInterface selectNetworkInterface() {
        new NetworkInfo().getRow(0).get().shouldBe(Condition.visible).click();
        return new NetworkInterface();
    }

    public NetworkInterfaceList.Menu getNetworkMenu() {
        return new NetworkInterfaceList().getMenuNetworkInterface(new NetworkInfo().getRow(0).get().$("button"));
    }

    public String getLocalIp() {
        return new NetworkInfo().getFirstValueByColumn(Column.IP);
    }

    public static class NetworkInfo extends Table {
        public NetworkInfo() {
            super(Column.IP);
        }
    }
}
