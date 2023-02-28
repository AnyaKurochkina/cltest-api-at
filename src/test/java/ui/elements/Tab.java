package ui.elements;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Tab implements TypifiedElement {
    @Getter
    private final SelenideElement element;
    private String text;

    public Tab(SelenideElement element) {
        this.element = element;
    }

    public static Tab byText(String text) {
        Tab tab = new Tab($x("//button[span[.='{}']]", text));
        tab.text = text;
        return tab;
    }

    public boolean isSelected() {
        return Boolean.valueOf(element.getAttribute("aria-selected"));
    }

    @Step("Переход на вкладку '{this.text}'")
    public void switchTo() {
        if (!isSelected()) element.scrollIntoView(false).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }
}
