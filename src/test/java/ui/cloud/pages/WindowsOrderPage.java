package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Keys;
import ui.cloud.tests.DropDown;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class WindowsOrderPage extends Product {
    DropDown platform = DropDown.name("Платформа");
    DropDown osVersion = DropDown.name("Версия ОС");
    DropDown group = DropDown.name("Группы");
    DropDown roleServer = DropDown.name("Роль сервера. (данное поле влияет на именование)");
    DropDown dataCentre = DropDown.name("Дата-центр");
    DropDown segment = DropDown.name("Сетевой сегмент");
    SelenideElement labelInput = $x("//div[label[starts-with(. , 'Метка')]]/div/input");

    final String label = UUID.randomUUID().toString().substring(17);

    public WindowsOrderPage() {
        labelInput.shouldBe(Condition.enabled);
        labelInput.sendKeys(Keys.CONTROL + "A");
        labelInput.sendKeys(Keys.BACK_SPACE);
        labelInput.setValue(label);
        platform.getElement().shouldBe(Condition.enabled);
    }
}
