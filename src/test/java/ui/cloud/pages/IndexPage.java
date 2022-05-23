package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class IndexPage {
    SelenideElement orderMoreBtn = $x("//button[contains(., 'Заказать еще3')]");

    public IndexPage() {
        orderMoreBtn.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }

    public NewOrderPage clickOrderMore(){
        orderMoreBtn.hover().click();
        return new NewOrderPage();
    }

}