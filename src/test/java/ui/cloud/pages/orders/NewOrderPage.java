package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Data;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Select;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;

@Data
public class NewOrderPage {

    private final Button orderBtn = Button.byText("Заказать");
    private final SelenideElement prebillingCostElement = $x("//*[@data-testid='new-order-details-price' and contains(.,',')]");
    private final SelenideElement opMemory = $x("//div[contains(text(),'Оперативная память')]");
    private final SelenideElement hardDrive = $x("//div[contains(text(),'Жесткий диск')]");
    private final SelenideElement hardDrive1 = $x("(//div[contains(text(),'Жесткий диск')])[1]");
    private final SelenideElement hardDrives = $x("//div[contains(text(),'Жесткие диски')]");
    private final SelenideElement processor = $x("//div[contains(text(),'Процессор')]");
    private final SelenideElement windowsOS = $x("//div[contains(text(),'ОС Windows')]");
    private final SelenideElement linuxOS = $x("//div[contains(text(),'ОС linux')]");
    protected Input countInput = Input.byLabel("Количество");
    protected Input labelInput = Input.byLabel("Метка");
    protected Select segmentSelect = Select.byLabel("Сетевой сегмент");
    protected Select platformSelect = Select.byLabel("Платформа");
    protected Select osVersionSelect = Select.byLabel("Версия ОС");
    protected Select versionWildfly = Select.byLabel("Версия Wildfly");
    protected Select versionJava = Select.byLabel("Версия java");
    protected Select domain = Select.byLabel("Домен");
    protected Select flavorSelect = Select.byLabel("Конфигурация Core/RAM");
    protected Select roleSelect = Select.byLabel("Роль");
    protected Select groupSelect = Select.byLabel("Группы");
    protected Select group2Select = Select.byLabel("Группы",2);
    protected String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

    //раскрывающийся список
    public static SelenideElement getCalculationDetails() {
        return $x("(//div[text()='Детали заказа'])[2]");
    }

    //Заголовок всего окна
    public static SelenideElement getCalculationDetailsHeader() {
        return $x("//div[text()='Детали заказа']");
    }

    public static String getFlavor(Flavor flavor) {
        return String.format("Core: %s, RAM: %s GB", flavor.getCpus(), flavor.getMemory());
    }

    public void orderClick() {
        orderBtn.click();
    }

    @Step("Проверка поля с входящими и ожидаемыми значениями")
    public void autoChangeableFieldCheck(Input input, String value, String exValue) {
        input.setValue(value);
        Assertions.assertEquals(exValue, input.getValue());
    }

    @Step("Проверка недоступности кнопки 'Заказать'")
    public void checkOrderDisabled() {
        orderBtn.getButton().shouldBe(Condition.disabled);
    }

    @Step("Проверка отображения деталей заказа")
    public void checkOrderDetails() {
        prebillingCostElement.shouldBe(Condition.visible);
        if (getCalculationDetails().exists()) {
            getCalculationDetails().shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible);
        getOpMemory().shouldBe(Condition.visible);
    }
}
