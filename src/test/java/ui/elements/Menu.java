package ui.elements;

import com.codeborne.selenide.SelenideElement;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Menu implements TypifiedElement{
    SelenideElement element;

    public Menu(SelenideElement element) {
        this.element = element.shouldBe(activeCnd).shouldBe(clickableCnd);
    }

    public void select(String item){
        element.click();
        $x("//ul/li[.='{}']", item).shouldBe(activeCnd).shouldBe(clickableCnd).click();
    }
}
