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
        text = element.getText();
    }

    public static Tab byText(String text) {
        return new Tab($x("//button[span[.='{}']]", text));
    }

    @Step("Получение значения, выбрана ли вкладка '{this.text}'")
    public boolean isSelected() {
        return Boolean.valueOf(element.getAttribute("aria-selected"));
    }

    @Step("Переход на вкладку '{this.text}'")
    public void switchTo() {
        if (!isSelected()) element.scrollIntoView(false).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }
}
