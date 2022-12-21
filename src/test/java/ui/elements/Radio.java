package ui.elements;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Radio implements TypifiedElement {
    SelenideElement radio;

    public Radio(SelenideElement radio) {
        this.radio = radio;
    }

    @Step("Получение Radio по value {value}")
    public static Radio byValue(String value){
        return new Radio($x("//input[@value='{}']", value));
    }

    public boolean isChecked(){
        return radio.isSelected();
    }

    public void checked(){
        radio.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        Assertions.assertTrue(isChecked());
    }
}
