package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;


public class Product {
    SelenideElement orderBtn = $x("//button[.='Заказать']");

    public void orderClick() {
        orderBtn.shouldBe(activeCnd).hover()
                .shouldBe(clickableCnd).click();
    }
}
