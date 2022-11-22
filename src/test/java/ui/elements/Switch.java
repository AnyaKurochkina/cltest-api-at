package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Switch implements TypifiedElement{
    private SelenideElement label;

    public Switch(SelenideElement element) {
        this.label = element;
    }

    public static Switch byLabel(String label){
        return new Switch($x("//*[.='{}']/label[@role='switch']", label));
    }

    public boolean isEnabled(){
        return label.is(Condition.attribute("aria-checked","true"));
    }

    public void setEnabled(boolean enabled){
        if(isEnabled() != enabled)
            label.shouldBe(clickableCnd).click();
        Assertions.assertEquals(enabled, isEnabled());
    }

}
