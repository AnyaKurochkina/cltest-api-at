package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;

public class CommonChecks {

    private final SelenideElement costDay = $x("//*[@data-testid='new-order-details-price']");


    public boolean isCostDayContains(String symbol) {
        return Objects.requireNonNull(costDay.getAttribute("textContent")).contains(symbol);
    }

}
