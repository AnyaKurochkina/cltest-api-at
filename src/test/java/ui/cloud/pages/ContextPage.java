package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.Tab;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$x;

public class ContextPage {

    private final SelenideElement userContext = $x("(//div[contains(@class,'UserContextButtonstyles')])[1]");

    @Step("Открытие формы выбора контекста")
    public ContextPage openUserContext() {
        userContext.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return this;
    }

    @Step("Выбор контекста организации")
    public ContextPage selectOrgContext() {
        Tab.byText("Все").switchTo();
        $x("(//div[@role='dialog']//table/tbody//tr//p[@color])[1]").click();
        return this;
    }
}
