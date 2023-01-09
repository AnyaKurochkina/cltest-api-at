package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$x;
import static api.Tests.clickableCnd;

public class ProductsPage {
    ElementsCollection products = $$x("//img/ancestor::button//h4");

    public void selectProduct(String product){
        products.find(Condition.exactText(product)).hover().shouldBe(clickableCnd).click();
    }
}
