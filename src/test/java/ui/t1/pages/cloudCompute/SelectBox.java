package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import ui.elements.DropDown;
import ui.elements.Radio;
import ui.elements.Select;
import ui.elements.TypifiedElement;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class SelectBox implements TypifiedElement {
    SelenideElement select;

    private SelectBox(SelenideElement element) {
        element.shouldBe(Condition.visible).scrollIntoView(scrollCenter);
        this.select = element;
    }

    public static void setMarketPlaceImage(Image image) {
        Radio.byValue("MarketPlace").checked();
        SelectBox selectBox = new SelectBox($x("//*[.='{}']/parent::*//*[name()='svg']", image.os));
        selectBox.select(image.version);
    }

    public static void setUserImage(String image) {
        Radio.byValue("Пользовательские").checked();
        new Select(TypifiedElement.getNearElement("select", "*[.='Пользовательские']").parent()).set(image);
    }

    private void select(String text) {
        select.parent().parent().parent().click();
        select.click();
        $x("//*[@title = '{}']", text).shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd)
                .click();
    }

    @AllArgsConstructor
    public static class Image {
        String os;
        String version;
    }
}
