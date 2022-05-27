package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Keys;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class WindowsOrderPage extends Product {
    DropDown platform = DropDown.byLabel("Платформа");
    DropDown osVersion = DropDown.byLabel("Версия ОС");
    DropDown group = DropDown.byLabel("Группы");
    DropDown roleServer = DropDown.byLabel("Роль сервера. (данное поле влияет на именование)");
    DropDown dataCentre = DropDown.byLabel("Дата-центр");
    DropDown segment = DropDown.byLabel("Сетевой сегмент");
    DropDown configure = DropDown.byLabel("Конфигурация Core/RAM");
    SelenideElement labelInput = $x("//div[label[starts-with(. , 'Метка')]]/div/input");

    final String label = UUID.randomUUID().toString().substring(17);

    public WindowsOrderPage() {
        new Input(labelInput).setValue(label);
        platform.getElement().shouldBe(Condition.enabled);
    }
}
