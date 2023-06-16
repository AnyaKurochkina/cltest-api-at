package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static core.helper.StringUtils.$x;

public class RadioGroup implements TypifiedElement{
    SelenideElement radio;

    public RadioGroup(SelenideElement radio) {
        this.radio = radio;
    }

    public static RadioGroup byLabel(String label){
        return byLabel(label, 1);
    }

    @Step("Получение RadioGroup по label {label} с индексом {index}")
    public static RadioGroup byLabel(String label, int index){
        return new RadioGroup($x("(//*[starts-with(text(),'{}')]/..//*[@role='radiogroup'])" + postfix, label, TypifiedElement.getIndex(index)).shouldBe(Condition.visible));
    }

    public static RadioGroup byFieldsetLabel(String label){
        return new RadioGroup($x("//*[text()='{}']/parent::fieldset", label));
    }

    public static RadioGroup bуId(String id){
        return new RadioGroup($x("//*[@role='radiogroup' and @id='{}']", id).shouldBe(Condition.visible));
    }

    public void select(String value){
        radio.$$("label").filter(Condition.exactText(value)).first().click();
    }
}
