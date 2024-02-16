package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.Select;

import java.time.Duration;

import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$$x;

@Getter
public class ProductsPage {
    private final Select categorySelect = Select.byLabel("Категория");
    private final Button applyButton = Button.byText("Применить");
    private final ElementsCollection products = $$x("//img/ancestor::button//h4");
    private final ElementsCollection expandButtons = $$x("//h4[contains(text(),'Посмотреть еще')]/ancestor::button");

    public ProductsPage() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

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
            Waiting.find(() -> expandButtons.first().exists(), Duration.ofSeconds(10));
        } catch (AssertionError ignored) {
        }
        for (SelenideElement button : expandButtons) {
            button.hover().shouldBe(clickableCnd).click();
        }
        return this;
    }

    @Step("Проверка, что отображается продукт '{title}'")
    public boolean isProductDisplayed(String title) {
        expandProductsList();
        return products.find(Condition.exactText(title)).isDisplayed();
    }
}
