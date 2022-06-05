package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import lombok.SneakyThrows;
import models.orderService.products.Windows;
import ui.elements.Dialog;
import ui.elements.DropDown;

import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

public class WindowsPage extends IProductPage {

    public WindowsPage(Windows product) {
        super(product);
    }
    @SneakyThrows
    public void delete()  {
        runActionWithParameters("Виртуальная машина", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            dlgActions.getDialog().$x("descendant::button[.='Удалить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlgActions.getDialog().shouldNotBe(Condition.visible);
        },false);
        waitChangeStatus();
        checkLastAction();
        btnGeneralInfo.click();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_DELETED);
    }

    public void start() {
        runActionWithoutParameters("Виртуальная машина", "Включить");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }

    public void restart() {
        runActionWithoutParameters("Виртуальная машина", "Перезагрузить по питанию");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }

    public void changeConfiguration() {
        runActionWithoutParameters("Виртуальная машина", "Изменить конфигурацию");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }
    @SneakyThrows
    public void discActAdd()  {
        runActionWithParameters("Дополнительные диски", "Добавить диск", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", "11");
            DropDown.byLabel("Буква").selectByValue("S");
            DropDown.byLabel("Файловая система").selectByValue("refs");
            dlg.getDialog().$x("descendant::button[.='Подтвердить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlg.getDialog().shouldNotBe(Condition.visible);
            Waiting.sleep(3000);

        },true);
        waitChangeStatus();
        checkLastAction();
    }

    public void discActExpand() {
        runActionWithoutParameters("Виртуальная машина", "Расширить диск");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }
    @SneakyThrows
    public void discActOff()  {
        runActionWithParameters2("Диск", "Отключить в ОС", () -> {
            Dialog dlg = new Dialog("Отключить в ОС");
            dlg.getDialog().$x("descendant::button[.='Подтвердить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlg.getDialog().shouldNotBe(Condition.visible);
            Waiting.sleep(3000);
        },true);
        waitChangeStatus();
        checkLastAction();
    }
    @SneakyThrows
    public void discActOn() {
        runActionWithParameters2("Диск", "Подключить в ОС", () -> {
            Dialog dlg = new Dialog("Подключить в ОС");
            dlg.getDialog().$x("descendant::button[.='Подтвердить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlg.getDialog().shouldNotBe(Condition.visible);
            Waiting.sleep(3000);
        },true);
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }

    public void discActDelete() {
        runActionWithoutParameters("Виртуальная машина", "Удалить");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }

    public void vmActCheckConfig() {
        runActionWithoutParameters("Виртуальная машина", "Проверить конфигурацию");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
    }

    public void stopSoft() {
        runActionWithoutParameters("Виртуальная машина", "Выключить");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_OFF);
    }

    public void stopHard() {
        runActionWithoutParameters("Виртуальная машина", "Выключить принудительно");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_OFF);
    }

    public void turnOnDeleteProtection() {
        runActionWithoutParameters("Виртуальная машина", "Защита от удаления");
        waitChangeStatus();
        checkLastAction();
        new VirtualMachine().open().checkPowerStatus(VirtualMachine.POWER_STATUS_OFF);
    }
    @SneakyThrows
    public void addDisk() {
        runActionWithParameters("Дополнительные диски", "Добавить диск", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", "11");
            DropDown.byLabel("Буква").selectByValue("S");
            DropDown.byLabel("Файловая система").selectByValue("refs");
        },true);
        waitChangeStatus();
        checkLastAction();
    }
}
