package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.elements.Dialog;
import ui.elements.Select;
import ui.elements.Table;

import static ui.t1.pages.cloudEngine.compute.Disk.DiskInfo.COLUMN_NAME;

public class Vm extends IProductT1Page<Vm> {

    public Disk selectDisk(String disk) {
        new Disk.DiskInfo().getRowByColumnValue(COLUMN_NAME, disk).get().shouldBe(Condition.visible).click();
        return new Disk();
    }

    public void attachIp(String ip) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить публичный IP", "Подтвердить", () ->
                Dialog.byTitle("Подключить публичный IP")
                        .setSelectValue("Сетевой интерфейс", Select.RANDOM_VALUE)
                        .setSelectValue("Публичный IP", ip));
    }

    public void getLink() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Получить ссылку на консоль");
    }

    public void stop() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Остановить");
        checkPowerStatus(VirtualMachine.POWER_STATUS_OFF);
    }

    public void start() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Запустить");
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }

    public NetworkInterface selectNetworkInterface() {
        new NetworkInfo().getRow(0).get().shouldBe(Condition.visible).click();
        return new NetworkInterface();
    }

    public static class NetworkInfo extends Table {
        public static final String COLUMN_IP = "IP";

        public NetworkInfo() {
            super(COLUMN_IP);
        }
    }
}
