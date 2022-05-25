package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

public class NewOrderPage {
    SelenideElement submitBtn = $x("//button[contains(., 'Посмотреть еще')]");
    ElementsCollection products = $$x("//button[img]");

    public NewOrderPage() {
        submitBtn.shouldBe(activeCnd).hover()
                .shouldBe(clickableCnd).click();
    }

    public void selectProduct(String product){
        products.find(Condition.exactText(product)).$("img").hover()
                .shouldBe(clickableCnd).click();
    }
}
