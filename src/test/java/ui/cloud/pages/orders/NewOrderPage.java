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
    protected final Input countInput = Input.byLabel("Количество");
    protected final Input labelInput = Input.byLabel("Метка");
    protected final Input nameVm = Input.byLabel("Имя ВМ");
    protected final Select segmentSelect = Select.byLabel("Сетевой сегмент");
    protected final Select platformSelect = Select.byLabel("Платформа");
    protected final Select osVersionSelect = Select.byLabel("Версия ОС");
    protected final Select versionKafka = Select.byLabel("Версия Apache Kafka");
    protected final Select versionWildfly = Select.byLabel("Версия Wildfly");
    protected final Select versionJava = Select.byLabel("Версия java");
    protected final Select domain = Select.byLabel("Домен");
    protected final Select flavorSelect = Select.byLabel("Конфигурация Core/RAM");
    protected final Select roleSelect = Select.byLabel("Роль");
    protected final Select groupSelect = Select.byLabel("Группы");
    protected final Select group2Select = Select.byLabel("Группы", 2);
    protected final Select groupManagerSelect = Select.byLabel("Manager");
    protected final Select groupAdministratorSelect = Select.byLabel("Administrator");
    protected final String labelValue = "AT-UI-" + UUID.randomUUID().toString().substring(24);

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
        prebillingCostElement.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        if (getCalculationDetails().exists()) {
            getCalculationDetails().shouldBe(Condition.visible.because("Должно отображаться сообщение")).shouldBe(Condition.enabled).click();
        }
        getProcessor().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        getOpMemory().shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }
}
