package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class SelectBox {
    SelenideElement select;

    private SelectBox(SelenideElement element) {
        element.shouldBe(Condition.visible).scrollIntoView(scrollCenter);
        this.select = element;
    }

    public static void setMarketPlaceImage(Image image) {
        SelectBox selectBox = new SelectBox($x("//*[.='{}']/parent::*//*[name()='svg']", image.os));
        selectBox.select(image.version);
    }

    public static void setUserImage(String image) {
        //Todo: реализовать селект
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
