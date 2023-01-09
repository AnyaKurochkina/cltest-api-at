package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Data;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.Input;

import static com.codeborne.selenide.Selenide.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;

@Data
public class Product {
    private final SelenideElement orderBtn = $x("//button[.='Заказать']");
    private final SelenideElement loadOrderPricePerDay = $x("//*[@data-testid='new-order-details-price' and contains(.,',')]");

    private final SelenideElement opMemory = $x("//div[contains(text(),'Оперативная память')]");
    private final SelenideElement hardDrive = $x("//div[contains(text(),'Жесткий диск')]");
    private final SelenideElement processor = $x("//div[contains(text(),'Процессор')]");
    private final SelenideElement windowsOS = $x("//div[contains(text(),'ОС Windows')]");
    private final SelenideElement linuxOS = $x("//div[contains(text(),'ОС linux')]");

    public static SelenideElement getCalculationDetails() {
        return $x("(//div[text()='Детали заказа'])[2]");
    }

    public void orderClick() {
        orderBtn.shouldBe(activeCnd).hover()
                .shouldBe(clickableCnd).click();
    }

    public static String getFlavor(Flavor flavor) {
        return String.format("Core: %s, RAM: %s GB", flavor.getCpus(), flavor.getMemory());
    }

    @Step("Проверка поля с входящими и ожидаемыми значениями")
    public void autoChangeableFieldCheck(Input input, String value, String exValue) {
        input.setValue(value);
        Assertions.assertEquals(exValue, input.getValue());
    }
}
