package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import models.orderService.interfaces.IProduct;
import models.orderService.products.Windows;
import models.portalBack.AccessGroup;
import ui.cloud.tests.DropDown;

import static com.codeborne.selenide.Selenide.closeWebDriver;

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
    }

    public void start() {
        runActionWithoutParameters("Виртуальная машина", "Включить");
        waitChangeStatus();
        checkLastAction();
    }

    public void restart() {
        runActionWithoutParameters("Виртуальная машина", "Перезагрузить по питанию");
        waitChangeStatus();
        checkLastAction();
    }

    public void stopSoft() {
        runActionWithoutParameters("Виртуальная машина", "Выключить");
        waitChangeStatus();
        checkLastAction();
    }

    public void stopHard() {
        runActionWithoutParameters("Виртуальная машина", "Выключить принудительно");
        waitChangeStatus();
        checkLastAction();
    }

    public void addDisk() {
        runActionWithParameters("Дополнительные диски", "Добавить диск", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", "11");
            DropDown.name("Буква").selectByValue("S");
            DropDown.name("Файловая система").selectByValue("refs");
        });
        waitChangeStatus();
        checkLastAction();
    }

}
