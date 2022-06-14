package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import models.orderService.products.Windows;
import ui.elements.Dialog;
import ui.elements.DropDown;

import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

public class WindowsPage extends IProductPage {

    public WindowsPage(Windows product) {
        super(product);
    }

    public void delete() {
        runActionWithParameters("Виртуальная машина", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            dlgActions.getDialog().$x("descendant::button[.='Удалить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlgActions.getDialog().shouldNotBe(Condition.visible);
        });
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

    public void addDisk() {
        runActionWithParameters("Дополнительные диски", "Добавить диск", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", "11");
            DropDown.byLabel("Буква").selectByValue("S");
            DropDown.byLabel("Файловая система").selectByValue("refs");
        });
        waitChangeStatus();
        checkLastAction();
    }
}
