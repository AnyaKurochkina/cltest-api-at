package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Data;
import models.subModels.Flavor;

import static com.codeborne.selenide.Selenide.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

@Data
public class Product {
    private final SelenideElement orderBtn = $x("//button[.='Заказать']");
    private final SelenideElement loadOrderPricePerDay = $x("//*[@data-testid='new-order-details-price' and contains(.,',')]");

    public void orderClick() {
        orderBtn.shouldBe(activeCnd).hover()
                .shouldBe(clickableCnd).click();
    }

    public static String getFlavor(Flavor flavor){
        return String.format("Core: %s, RAM: %s GB", flavor.getCpus(), flavor.getMemory());
    }
}
