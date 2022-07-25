package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static core.helper.StringUtils.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

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
        element.shouldBe(activeCnd).hover();
        if(element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        if(element.getText().equals(value))
            return;
        element.shouldBe(clickableCnd).click();
        $x("//ul/li[text()='{}']", value)
                .shouldBe(Condition.enabled)
                .click();
    }

    public void selectByValue(String value){
        element.shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd);
        if(element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//ul/li[@data-value='{}']", value)
                .shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd)
                .click();
    }

    public void selectByTitle(String value){
        element.scrollIntoView("{block: 'center'}");
        element.shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd);
        if(element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//*[@title = '{}']", value)
                .shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd)
                .click();
    }
}
