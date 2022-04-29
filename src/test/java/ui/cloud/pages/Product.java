package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;


public class Product {
    SelenideElement orderBtn = $x("//button[.='Заказать']");

    public void orderClick() {
        orderBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled).hover()
                .shouldNotBe(Condition.cssValue("cursor", "default")).click();
    }
}
