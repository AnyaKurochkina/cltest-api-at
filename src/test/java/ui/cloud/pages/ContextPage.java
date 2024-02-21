package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.Tab;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.format;

public class ContextPage {

    private final SelenideElement userContext = $x("(//div[contains(@class,'UserContextButtonstyles')])[1]");

    @Step("Открытие формы выбора контекста")
    public ContextPage openUserContext() {
        userContext.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return this;
    }

    @Step("Выбор контекста организации")
    public ContextPage setOrgContext() {
        Tab.byText("Все").switchTo();
        $x("(//div[@role='dialog']//table/tbody//tr//p[@color])[1]").hover()
                .shouldBe(Condition.enabled).click();
        return this;
    }

    @Step("Выбор контекста '{value}'")
    public ContextPage setContext(String value) {
        Tab.byText("Все").switchTo();
        $x("(//div[@role='dialog']//div[text()='{}'])[1]", value).hover()
                .shouldBe(Condition.enabled.because(format("Отображается элемент орг. структуры '{}'", value))).click();
        return this;
    }

}
