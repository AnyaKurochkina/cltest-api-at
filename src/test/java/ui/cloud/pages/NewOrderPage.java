package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Data;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Select;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$x;

@Data
public class NewOrderPage {

    private final SelenideElement orderBtn = $x("//button[.='Заказать']");
    private final SelenideElement loadOrderPricePerDay = $x("//*[@data-testid='new-order-details-price' and contains(.,',')]");
    private final SelenideElement opMemory = $x("//div[contains(text(),'Оперативная память')]");
    private final SelenideElement hardDrive = $x("//div[contains(text(),'Жесткий диск')]");
    private final SelenideElement processor = $x("//div[contains(text(),'Процессор')]");
    private final SelenideElement windowsOS = $x("//div[contains(text(),'ОС Windows')]");
    private final SelenideElement linuxOS = $x("//div[contains(text(),'ОС linux')]");
    protected Input countInput = Input.byLabel("Количество");
    protected Input labelInput = Input.byLabel("Метка");
    protected Select segmentSelect = Select.byLabel("Сетевой сегмент");
    protected Select dataCentreSelect = Select.byLabel("Дата-центр");
    protected Select platformSelect = Select.byLabel("Платформа");
    protected Select osVersionSelect = Select.byLabel("Версия ОС");
    protected Select flavorSelect = Select.byLabel("Конфигурация Core/RAM");
    protected Select groupSelect = Select.byLabel("Группы");

    public static SelenideElement getCalculationDetails() {
        return $x("(//div[text()='Детали заказа'])[2]");
    }

    public static String getFlavor(Flavor flavor) {
        return String.format("Core: %s, RAM: %s GB", flavor.getCpus(), flavor.getMemory());
    }

    public void orderClick() {
        orderBtn.shouldBe(activeCnd).hover()
                .shouldBe(clickableCnd).click();
    }

    @Step("Проверка поля с входящими и ожидаемыми значениями")
    public void autoChangeableFieldCheck(Input input, String value, String exValue) {
        input.setValue(value);
        Assertions.assertEquals(exValue, input.getValue());
    }
}
