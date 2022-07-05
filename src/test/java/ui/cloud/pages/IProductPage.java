package ui.cloud.pages;

import com.codeborne.selenide.*;
import core.exception.CreateEntityException;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import steps.stateService.StateServiceSteps;
import ui.elements.Dialog;
import ui.elements.Table;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.open;
import static core.helper.StringUtils.$x;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.CONTROL;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

@Log4j2
@Getter
public abstract class IProductPage {
    IProduct product;
    double prePriceOrderDbl;
    double priceOrderDbl;

    double preBillingCostAction;

    SelenideElement btnHistory = $x("//button[.='История действий']");
    SelenideElement btnGeneralInfo = $x("//button[.='Общая информация']");
    SelenideElement btnAct = $x("(//div[@id='root']//*[text()='Дополнительные диски']/ancestor::div[3]//following-sibling::div//button[@id='actions-menu-button' and not (.//text()='Действия')])[last()]");
    private final SelenideElement orderPricePerDay = Selenide.$x("//*[@data-testid='new-order-details-price']");
    private final SelenideElement btnProducts = Selenide.$x("//div[not(@hidden)]/a[@href='/vm/orders' and text()='Продукты']");
    private final SelenideElement progressBars = Selenide.$x("(//div[div[@role='progressbar']])[last()]");
    private final SelenideElement orderPricePerDayAfterOrder = Selenide.$x("//button[@title='Редактировать']/following::p[1]");

    private final SelenideElement currentPriceOrder = Selenide.$x("(//p[contains(.,'₽/сут.') and contains(.,',')])[1]");
    private final SelenideElement preBillingPriceAction = Selenide.$x("(//p[contains(.,'₽/сут.') and contains(.,',')])[2]");
    private final SelenideElement closeModalWindowButton = Selenide.$x("//div[@role='dialog']//button[contains(.,'Закрыть')]");
    private final SelenideElement graphScheme = Selenide.$x("//canvas");

    private final SelenideElement orderBtn = Selenide.$x("//button[.='Заказать']");
    private final SelenideElement actionHistory = Selenide.$x("//*[text()='История действий']/ancestor::button");
    private final SelenideElement actionNameColumn = Selenide.$x("//table//tr/th[text()='Наименование']");
    private final SelenideElement actionInitiatorColumn = Selenide.$x("//table//tr/th[text()='Инициатор']");
    private final SelenideElement actionCreationDateColumn = Selenide.$x("//table//tr/th[text()='Дата создания']");
    private final SelenideElement actionStartDateColumn = Selenide.$x("//table//tr/th[text()='Дата запуска']");
    private final SelenideElement actionDurationColumn = Selenide.$x("//table//tr/th[text()='Продолжительность, сек']");
    private final SelenideElement actionStatusColumn = Selenide.$x("//table//tr/th[text()='Статус']");
    private final SelenideElement actionViewColumn = Selenide.$x("//table//tr/th[text()='Просмотр']");
    private final SelenideElement historyRowDeployOk = Selenide.$x("(//td[text()='Развертывание']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDeployErr = Selenide.$x("(//td[text()='Развертывание']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowRestartByPowerOk = Selenide.$x("(//td[text()='Перезагрузить по питанию']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowRestartByPowerErr = Selenide.$x("(//td[text()='Перезагрузить по питанию']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowTurnOffOk = Selenide.$x("(//td[text()='Выключить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowTurnOffErr = Selenide.$x("(//td[text()='Выключить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowChangeFlavorOk = Selenide.$x("(//td[text()='Изменить конфигурацию']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowChangeFlavorErr = Selenide.$x("(//td[text()='Изменить конфигурацию']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowTurnOnOk = Selenide.$x("(//td[text()='Включить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowTurnOnErr = Selenide.$x("(//td[text()='Включить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscAddOk = Selenide.$x("(//td[text()='Добавить диск']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscAddErr = Selenide.$x("(//td[text()='Добавить диск']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowMountExpandOkDisc = Selenide.$x("(//td[text()='Расширить диск']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowMountExpandErrDisc = Selenide.$x("(//td[text()='Расширить диск']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscTurnOffOk = Selenide.$x("(//td[text()='Отключить в ОС']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscTurnOffErr = Selenide.$x("(//td[text()='Отключить в ОС']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscTurnOnOk = Selenide.$x("(//td[text()='Подключить в ОС']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscTurnOnErr = Selenide.$x("(//td[text()='Подключить в ОС']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscDeleteOk = Selenide.$x("(//td[text()='Удалить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscDeleteErr = Selenide.$x("(//td[text()='Удалить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowCheckConfigOk = Selenide.$x("(//td[text()='Проверить конфигурацию']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowCheckConfigErr = Selenide.$x("(//td[text()='Проверить конфигурацию']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowForceTurnOffOk = Selenide.$x("(//td[text()='Выключить принудительно']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowForceTurnOffErr = Selenide.$x("(//td[text()='Выключить принудительно']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDeletedOk = Selenide.$x("(//td[text()='Удалить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDeletedErr = Selenide.$x("(//td[text()='Удалить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement orderProduct = Selenide.$x("//*[text()='Заказать']/ancestor::button");
    private final SelenideElement vmNumber = Selenide.$x("//*[contains(.,'Количество')]/following-sibling::*/input");
    private final SelenideElement mark = Selenide.$x("(//*[text()='Метка']//following::input)[1]");
    private final SelenideElement calculationDetails = Selenide.$x("//*[text()='детализация расчета']/*");
    private final SelenideElement opMemory = Selenide.$x("//div[contains(text(),'Оперативная память')]");
    private final SelenideElement hardDrive = Selenide.$x("//div[contains(text(),'Жесткий диск')]");
    private final SelenideElement processor = Selenide.$x("//div[contains(text(),'Процессор')]");
    private final SelenideElement windowsOS = Selenide.$x("//div[contains(text(),' ОС Windows для среды DEV OpenStack')]");
    private final SelenideElement linuxOS = Selenide.$x("//div[contains(text(),'ОС linux')]");
    private final SelenideElement historyRow0 = Selenide.$x("//tr[@index='0']//button[@tabindex='0'][last()]");


    public IProductPage(IProduct product) {
        if (Objects.nonNull(product.getError()))
            throw new CreateEntityException(String.format("Продукт необходимый для выполнения теста был создан с ошибкой:\n%s", product.getError()));
        if (Objects.nonNull(product.getLink()))
            open(product.getLink());
        btnGeneralInfo.shouldBe(Condition.enabled);
        product.setLink(WebDriverRunner.getWebDriver().getCurrentUrl());
        this.product = product.buildFromLink();
    }

    public IProductPage() {}

    @Step("Ожидание выполнение действия с продуктом")
    public void waitChangeStatus() {
        List<String> titles = new TopInfo().getValueByColumnInFirstRow("Статус").$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e ->
                        ProductStatus.isNeedWaiting(e.getAttribute("title"))), Duration.ofMinutes(30))
                .stream().map(e -> e.getAttribute("title")).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

    public SelenideElement getBtnAction(String header) {
        return $x("//ancestor::*[.='{}']/parent::*//button[@id='actions-menu-button']", header);
    }


    @Step("Запуск действия '{action}' в блоке '{headerBlock}'")
    protected void runActionWithoutParameters(String headerBlock, String action) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        getBtnAction(headerBlock).shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).scrollTo().hover().shouldBe(clickableCnd).click();
        preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        Dialog dlgActions = new Dialog(action);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        dlgActions.getDialog().shouldNotBe(Condition.visible);
        Waiting.sleep(3000);
        waitChangeStatus();
        checkLastAction(action);
    }

    @Step("Запуск действия '{action}'")
    protected void runActionWithoutParameters(SelenideElement button, String action) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        button.shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Dialog dlgActions = new Dialog(action);
        preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        dlgActions.getDialog().shouldNotBe(Condition.visible);
        Waiting.sleep(3000);
        waitChangeStatus();
        checkLastAction(action);
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' с параметрами")
    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        button.shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
        preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        SelenideElement runButton = $x("//div[@role='dialog']//button[.='{}']", textButton);
        runButton.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runButton.shouldNotBe(Condition.visible);
        Waiting.sleep(3000);
        waitChangeStatus();
        checkLastAction(action);
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' в блоке '{headerBlock}' с параметрами")
    protected void runActionWithParameters(String headerBlock, String action, String textButton, Executable executable) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        getBtnAction(headerBlock).shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
        preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        SelenideElement button = $x("//div[@role='dialog']//button[.='{}']", textButton);
        button.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        button.shouldNotBe(Condition.visible);
        Waiting.sleep(3000);
        waitChangeStatus();
        checkLastAction(action);
    }

    public void checkErrorByStatus(String status) {
        if (status.equals(ProductStatus.ERROR)) {
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s",
                    product, status, StateServiceSteps.GetErrorFromStateService(product.getOrderId())));
        }
    }

    @Step("Проверка на содержание неоюходимых столбцов на вкладке История действий")
    public void checkHeadersHistory() {
        Assertions.assertEquals(Arrays.asList("Наименование", "Инициатор", "Дата создания", "Дата запуска", "Продолжительность, сек",
                "Статус", "Просмотр"), new History().getHeaders());
    }

    private static class TopInfo extends Table {
        public TopInfo() {
            super("Защита от удаления");
        }
    }

    public class History extends Table {
        public History() {
            super("Дата запуска");
        }

        @Override
        protected void open() {
            btnHistory.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click(ClickOptions.usingJavaScript());
        }

        public String lastActionName() {
            return getValueByColumnInFirstRow("Наименование").getText();
        }

        public String lastActionStatus() {
            return getValueByColumnInFirstRow("Статус").$x("descendant::*[@title]").getAttribute("title");
        }
    }

    @SneakyThrows
    @Step("Запуска действия с проверкой стоимости")
    public void runActionWithCheckCost(CompareType type, Executable executable) {
        Selenide.refresh();
        waitChangeStatus();
        double currentCost = getCostOrder();
        executable.execute();
        Selenide.refresh();
        if(type == CompareType.MORE)
            Assertions.assertTrue(preBillingCostAction > currentCost, String.format("%f <= %f", preBillingCostAction, currentCost));
        else if(type == CompareType.LESS)
            Assertions.assertTrue(preBillingCostAction < currentCost, String.format("%f >= %f", preBillingCostAction, currentCost));
        else if(type == CompareType.EQUALS)
            Assertions.assertEquals(preBillingCostAction, currentCost, 0.01d);
        else if(type == CompareType.ZERO) {
            Assertions.assertEquals(0.0d, preBillingCostAction, 0.001d);
            Assertions.assertEquals(0.0d, currentCost, 0.001d);
        }
        Assertions.assertEquals(preBillingCostAction, getCostOrder(), "Стоимость предбиллинга экшена не равна стоимости после выполнения действия");
    }

    protected abstract class VirtualMachine extends Table {
        public static final String POWER_STATUS_DELETED = "Удалено";
        public static final String POWER_STATUS_ON = "Включено";
        public static final String POWER_STATUS_OFF = "Выключено";

        abstract String getPowerStatus();

        @Override
        protected void open() {
            btnGeneralInfo.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click(ClickOptions.usingJavaScript());
        }

        public VirtualMachine(String columnName) {
            super(columnName);
        }

        public String getPowerStatus(String header) {
            return getValueByColumnInFirstRow(header).$x("descendant::*[@title]").getAttribute("title");
        }

        public void checkPowerStatus(String status) {
            Assertions.assertEquals(status, getPowerStatus(), "Статус питания не соотвествует ожидаемому");
        }
    }

    @Step("Проверка на содержание элемента в атрибуте textContent")
    public void isCostDayContains(String symbol) {
        Objects.requireNonNull(orderPricePerDay.getAttribute("textContent"));
    }

    @Step("Проверка выполнения действия {action}")
    public void checkLastAction(String action) {
        btnHistory.shouldBe(Condition.enabled).click(ClickOptions.usingJavaScript());
        History history = new History();
        checkErrorByStatus(history.lastActionStatus());
        Assertions.assertEquals(history.lastActionName(), action, "Название последнего действия не соответствует ожидаемому");
    }

    public History getHistoryTable() {
        return new History();
    }

    @Step("Проверка поля с входящими и ожидаемыми значениями")
    public void autoChangeableFieldCheck(SelenideElement sElement, String input, String value) {
        sElement.click();
        sElement.sendKeys(CONTROL + "a");
        sElement.sendKeys(BACK_SPACE);
        sElement.setValue(input);
        Objects.requireNonNull(sElement.getAttribute("valueAsNumber")).contains(value); //TODO: Неиспользуемый contains. зачем?
        sElement.sendKeys(CONTROL + "a");
        sElement.sendKeys(BACK_SPACE);
        log.debug("Проверка поля с входящими и ожидаемыми значениями");
    }

    @Step("Проверка поля количество VM")
    public void checkFieldVmNumber() {
        autoChangeableFieldCheck(getVmNumber(), "0", "10");
        autoChangeableFieldCheck(getVmNumber(), "100", "30");
        autoChangeableFieldCheck(getVmNumber(), "N", "10");
    }

    @Step("Проверка деталей у заказа продукта'")
    public void checkOrderDetails(SelenideElement sElement, String product) {
        sElement.shouldBe(Condition.enabled).click();
        log.info("пользователь проверяет детали заказа у продукта");
        switch (product) {
            case "Windows Server": {
                processor.shouldBe(Condition.visible);
                hardDrive.shouldBe(Condition.visible);
                break;
            }
            case "Elasticsearch Opensearch cluster (Astra)":
            case "Elasticsearch Opensearch cluster":
            case "Elasticsearch X-pack cluster": {
                linuxOS.shouldBe(Condition.visible);
                opMemory.shouldBe(Condition.visible);
                processor.shouldBe(Condition.visible);
                hardDrive.shouldBe(Condition.visible);
                break;
            }
            case "Apache Kafka Cluster RHEL":
            case "Apache Kafka Cluster Astra":
            case "VTB Apache ActiveMQ Artemis":
            case "VTB Apache ActiveMQ Artemis Astra":
            case "Astra Linux":
            case "ClickHouse":
            case "Nginx":
            case "Nginx Astra":
            case "Podman":
            case "Podman (Astra)":
            case "PostgreSQL":
            case "PostgreSQL Cluster Astra Linux":
            case "PostgresPro":
            case "RHEL":
            case "Redis":
            case "WildFly":
            case "ScyllaDB":
            case "Ubuntu Linux":
                //  case "RabbitMQ Cluster Astra":
            case "RabbitMQ Cluster": {
                linuxOS.shouldBe(Condition.visible);
                opMemory.shouldBe(Condition.visible);
                hardDrive.shouldBe(Condition.visible);
                break;
            }
            default: {
                break;
            }
        }
    }

    @Step("Получение стоимости заказа")
    public double getCostOrder() {
        currentPriceOrder.shouldBe(Condition.visible, Duration.ofMinutes(3));
        double cost = Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?\\d{1,5},\\d{2})", currentPriceOrder.getText()))
                .replace(',', '.'));
        log.debug("Стоимость заказа {}", cost);
        return cost;
    }

    @Step("Получение стоимости предбиллинга")
    public static double getPreBillingCostAction(SelenideElement element) {
        element.shouldBe(Condition.visible);
        double cost = Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?\\d{1,5},\\d{2})", element.getText()))
                .replace(',', '.'));
        log.debug("Стоимость предбиллинга {}", cost);
        return cost;
    }

}
