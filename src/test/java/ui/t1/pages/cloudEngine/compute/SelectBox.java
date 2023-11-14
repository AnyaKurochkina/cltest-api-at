package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import core.exception.NotFoundElementException;
import lombok.AllArgsConstructor;
import ui.elements.Button;
import ui.elements.Select;
import ui.elements.TypifiedElement;

import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;

public class SelectBox implements TypifiedElement {
    SelenideElement select;

    private SelectBox(SelenideElement element) {
        element.shouldBe(Condition.visible).scrollIntoView(scrollCenter);
        this.select = element;
    }

    public static void setOsImage(Image image) {
        Button.byText("Операционные системы").click();
        findImage(() -> {
            SelectBox selectBox = new SelectBox($x("//*[.='{}']/parent::*//*[name()='svg']", image.os));
            selectBox.select(image.version);
        }, image.os);
    }

    public static void setMarketplaceImage(String image) {
        Button.byText("Cloud Marketplace").click();
        Button.byText("Показать еще").click();
        findImage(() -> $$x("//span[starts-with(text(),'{}')]", image).filter(Condition.visible).get(0).click(), image);
        Button.byText("Использовать").click();
    }

    public static void setUserImage(String image) {
        if (!Button.byText("Пользовательские").isVisible()) {
            Select.byXpath("//*[@id = 'Cloud Marketplace']/following-sibling::div/button").set("Пользовательские");
        } else {
            Button.byId("Пользовательские").click();
        }
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

    private static void findImage(Runnable runnable, String image) {
        try {
            runnable.run();
        } catch (ElementNotFound e) {
            NotFoundElementException exception = new NotFoundElementException("Не найден образ {}", image);
            exception.addSuppressed(e);
            throw exception;
        }
    }
}
