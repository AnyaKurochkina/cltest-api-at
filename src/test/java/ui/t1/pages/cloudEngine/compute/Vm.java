package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.By;
import ui.elements.*;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.switchTo;
import static core.helper.StringUtils.$x;

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
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить публичный IP", "Подтвердить", () -> {
            Waiting.sleep(2000);
                Dialog.byTitle("Подключить публичный IP")
                        .setSelectValue("Сетевой интерфейс", Select.RANDOM_VALUE)
                        .setSelectValue("Публичный IP", ip);
        });
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

    @Step("Проверка подключения к консоли")
    public void checkConsole() {
        Button console = Button.byText("Консоль");
        console.click();
        Button.byText("Развернуть на полный экран").click();
        Button.byText("Выйти из полноэкранного режима").click();
        console.getButton().should(Condition.visible);
        Waiting.find(() -> {
            switchTo().defaultContent();
            String status = switchTo().frame($x("//*[@title='Консоль']")).findElement(By.id("noVNC_status")).getText();
            return status.contains("Connected (encrypted)");
        }, Duration.ofSeconds(30));
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
