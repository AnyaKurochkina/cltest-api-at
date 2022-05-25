package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static core.helper.StringUtils.$x;

public class DropDown implements TypifiedElement{
    @Getter
    protected SelenideElement element;

    public DropDown(SelenideElement element) {
        this.element = element;
    }

    public static DropDown byLabel(String name){
        return new DropDown($x("//div[label[text()='{}']]/div", name));
    }

    public void select(String value){
        element.shouldBe(Condition.visible).shouldBe(Condition.enabled).hover();
        if(element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        if(element.getText().equals(value))
            return;
        element.shouldNotBe(Condition.cssValue("cursor", "default")).click();
        $x("//ul/li[text()='{}']", value)
                .shouldBe(Condition.enabled)
                .click();
    }

    public void selectByValue(String value){
        element.shouldBe(Condition.visible).shouldBe(Condition.enabled)
                .hover().shouldNotBe(Condition.cssValue("cursor", "default"));
        if(element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//ul/li[@data-value='{}']", value)
                .shouldBe(Condition.enabled)
                .click();
    }
}
