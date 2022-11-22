package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class RadioGroup implements TypifiedElement{
    SelenideElement radio;

    public RadioGroup(SelenideElement radio) {
        this.radio = radio;
    }

    public static RadioGroup byLabel(String label){
        return new RadioGroup($x("//*[.='{}']/..//*[@role='radiogroup' and @id='port']", label).shouldBe(Condition.visible));
    }

    public static RadioGroup bId(String id){
        return new RadioGroup($x("//*[@role='radiogroup' and @id='{}']", id).shouldBe(Condition.visible));
    }

    public void select(String value){
        radio.$$("label").filter(Condition.exactText(value)).first().click();
    }
}
