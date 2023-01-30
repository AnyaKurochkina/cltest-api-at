package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Switch implements TypifiedElement{
    @Getter
    private final SelenideElement label;

    public Switch(SelenideElement element) {
        this.label = element;
    }

    @Step("Получение Switch по label {label}")
    public static Switch byLabel(String label){
        return new Switch($x("//*[.='{}']/label[@role='switch']", label));
    }

    @Step("Получение Switch по input name {name}")
    public static Switch byInputName(String name){
        return new Switch($x("//label[@role='switch'][input[@name='{}']]", name));
    }

    public boolean isEnabled(){
        return label.is(Condition.attribute("aria-checked","true"));
    }

    public void setEnabled(boolean enabled){
        if(isEnabled() != enabled)
            label.hover().shouldBe(clickableCnd).click();
        Assertions.assertEquals(enabled, isEnabled());
    }

}
