package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$x;
import static tests.Tests.clickableCnd;

public class ProductsPage {
    ElementsCollection products = $$x("//button//img");

    public void selectProduct(String product){
        products.find(Condition.exactText(product)).$("img").hover()
                .shouldBe(clickableCnd).click();
    }
}
