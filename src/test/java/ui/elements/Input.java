package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

public class Input implements TypifiedElement{
    SelenideElement input;

    public Input(SelenideElement input) {
        this.input = input;
        input.shouldBe(Condition.visible);
    }

    public void setValue(String value){
        input.sendKeys(Keys.CONTROL + "A");
        input.sendKeys(Keys.BACK_SPACE);
        input.setValue(value);
    }
}
