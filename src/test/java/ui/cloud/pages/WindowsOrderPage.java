package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.cloud.tests.DropDown;

@Getter
public class WindowsOrderPage {
    DropDown platform = DropDown.name("Платформа");
    DropDown osVersion = DropDown.name("Версия ОС");
    DropDown group = DropDown.name("Группы");
    DropDown roleServer = DropDown.name("Роль сервера. (данное поле влияет на именование)");
    DropDown dataCentre = DropDown.name("Дата-центр");
    DropDown segment = DropDown.name("Сетевой сегмент");

    public WindowsOrderPage() {
        platform.getElement().shouldBe(Condition.enabled);
    }
}
