package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.Select;

import java.time.Duration;

import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$$x;

public class ProductsPage {
    private final Select categorySelect = Select.byLabel("Категория");
    private final Button applyButton = Button.byText("Применить");
    ElementsCollection products = $$x("//img/ancestor::button//h4");
    ElementsCollection expandButtons = $$x("//h4[contains(text(),'Посмотреть еще')]/ancestor::button");

    @Step("Выбрать категорию '{value}'")
    public ProductsPage selectCategory(String value) {
        categorySelect.set(value);
        applyButton.click();
        return this;
    }

    @Step("Выбрать продукт '{product}'")
    public void selectProduct(String product) {
        products.find(Condition.exactText(product)).hover().shouldBe(clickableCnd).click();
    }

    @Step("Раскрыть список продуктов")
    public ProductsPage expandProductsList() {
        try {
            Waiting.find(() -> expandButtons.first().exists(), Duration.ofSeconds(3));
        } catch (Exception ignored) {}
        for (SelenideElement button : expandButtons) {
            button.hover().shouldBe(clickableCnd).click();
        }
        return this;
    }
}
