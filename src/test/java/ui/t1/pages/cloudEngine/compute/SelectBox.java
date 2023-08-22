package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import ui.elements.Button;
import ui.elements.Menu;
import ui.elements.Select;
import ui.elements.TypifiedElement;

import static com.codeborne.selenide.Selenide.$;
import static core.helper.StringUtils.$x;

public class SelectBox implements TypifiedElement {
    SelenideElement select;

    private SelectBox(SelenideElement element) {
        element.shouldBe(Condition.visible).scrollIntoView(scrollCenter);
        this.select = element;
    }

    public static void setOsImage(Image image) {
        Button.byText("Операционные системы").click();
        SelectBox selectBox = new SelectBox($x("//*[.='{}']/parent::*//*[name()='svg']", image.os));
        selectBox.select(image.version);
    }

    public static void setMarketplaceImage(Image image) {
        Button.byText("Cloud Marketplace").click();
        SelectBox selectBox = new SelectBox($x("//*[.='{}']/parent::*//*[name()='svg']", image.os));
        selectBox.select(image.version);
    }

    public static void setUserImage(String image) {
        if (!Button.byText("Пользовательские").isVisible()) {
            Menu.byElement($(By.className("overflow-menu-button-with-dropdown"))).select("Пользовательские");
        }
        Button.byId("Пользовательские").click();
        Select.byPlaceholder("выберите").setStart(image);
    }

    private void select(String text) {
        select.parent().parent().parent().click();
        new Select(select.parent().parent()).set(text);
    }

    @AllArgsConstructor
    public static class Image {
        String os;
        String version;
    }
}
