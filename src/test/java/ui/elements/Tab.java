package ui.elements;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Tab implements TypifiedElement {
    @Getter
    private final SelenideElement element;
    @SuppressWarnings("UnusedDeclaration")
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
        return Boolean.parseBoolean(element.getAttribute("aria-selected"));
    }

    @Step("Переход на вкладку '{this.text}'")
    public void switchTo() {
        if (!isSelected()) {
            Waiting.sleep(1000);
            element.scrollIntoView(false).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        }
    }
}
