package ui.cloud.pages;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import models.orderService.products.Windows;
import ui.elements.Dialog;
import ui.elements.DropDown;

import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

public class WindowsPage extends IProductPage {

    public WindowsPage(Windows product) {
        super(product);
    }
    
    public void delete()  {
        runActionWithParameters("Виртуальная машина", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            dlgActions.getDialog().$x("descendant::button[.='Удалить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlgActions.getDialog().shouldNotBe(Condition.visible);
        });
        btnGeneralInfo.click(ClickOptions.usingJavaScript());
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void start() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters("Виртуальная машина", "Включить");
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void restart() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Перезагрузить по питанию");
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void changeConfiguration() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters("Виртуальная машина", "Изменить конфигурацию");
    }
    
    public void discActAdd()  {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Дополнительные диски", "Добавить диск", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", "11");
            DropDown.byLabel("Буква").selectByValue("S");
            DropDown.byLabel("Файловая система").selectByValue("refs");
            dlg.getDialog().$x("descendant::button[.='Подтвердить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlg.getDialog().shouldNotBe(Condition.visible);
            Waiting.sleep(3000);
        });
    }

    public void discActExpand() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Расширить диск");
    }

    public void discActOff()  {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Диск", "Отключить в ОС", () -> {
            Dialog dlg = new Dialog("Отключить в ОС");
            dlg.getDialog().$x("descendant::button[.='Подтвердить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlg.getDialog().shouldNotBe(Condition.visible);
            Waiting.sleep(3000);
        });
    }

    public void discActOn() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Диск", "Подключить в ОС", () -> {
            Dialog dlg = new Dialog("Подключить в ОС");
            dlg.getDialog().$x("descendant::button[.='Подтвердить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlg.getDialog().shouldNotBe(Condition.visible);
            Waiting.sleep(3000);
        });
    }
    
    public void discActDelete() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Диск", "Удалить диск", () -> {
            Dialog dlg = new Dialog("Удалить");
            dlg.getDialog().$x("descendant::button[.='Подтвердить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            dlg.getDialog().shouldNotBe(Condition.visible);
            Waiting.sleep(3000);
        });
    }

    public void vmActCheckConfig() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Проверить конфигурацию");
    }

    public void stopSoft() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Выключить");
    }

    public void stopHard() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Выключить принудительно");
    }

    public void turnOnDeleteProtection() {
        runActionWithoutParameters("Виртуальная машина", "Защита от удаления");
    }

    public void addDisk() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Дополнительные диски", "Добавить диск", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", "11");
            DropDown.byLabel("Буква").selectByValue("S");
            DropDown.byLabel("Файловая система").selectByValue("refs");
        });
    }
    
    public class VirtualMachineTable extends VirtualMachine{
        public VirtualMachineTable() {
            super("Имя хоста");
        }
        
        @Override
        public String getPowerStatus(){
            return getPowerStatus("Питание");
        }
    }
}
