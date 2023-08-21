package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.TestProduct;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.*;

import java.util.List;

import static ui.elements.TypifiedElement.scrollCenter;

public class TestProductOrderPage extends IProductPage {
    private static final String BLOCK_VM = "Виртуальная машина";

    public TestProductOrderPage(TestProduct product) {
        super(product);
    }

    @Step("Проверка отображения действия '{actionTitle}'")
    public boolean isActionDisplayed(String actionTitle) {
        return Menu.byElement(getActionsMenuButton(BLOCK_VM)).isItemDisplayed(actionTitle);
    }

    @Step("Проверка отображения и доступности действия '{actionTitle}'")
    public boolean isActionDisplayedEnabled(String actionTitle) {
        return Menu.byElement(getActionsMenuButton(BLOCK_VM)).isItemDisplayedEnabled(actionTitle);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {}
}
