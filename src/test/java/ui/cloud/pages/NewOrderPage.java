package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class NewOrderPage {
    SelenideElement submitBtn = $x("//button[contains(., 'Посмотреть еще')]");
    ElementsCollection products = $$x("//button[img]");

    public NewOrderPage() {
        submitBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
    }

    public void selectProduct(String product){
        products.find(Condition.exactText(product)).$("img").click();
    }
}
