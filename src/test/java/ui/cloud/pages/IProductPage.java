package ui.cloud.pages;

import com.codeborne.selenide.*;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import steps.stateService.StateServiceSteps;
import ui.elements.Dialog;
import ui.elements.Table;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.open;
import static core.helper.StringUtils.$x;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.CONTROL;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

@Log4j2
@Getter
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IProductPage {
    TopInfo topInfo;
    IProduct product;
    double prePriceOrderDbl;
    double priceOrderDbl;




    SelenideElement btnHistory = $x("//button[.='История действий']");
    SelenideElement btnGeneralInfo = $x("//button[.='Общая информация']");
    SelenideElement btnAct = $x("(//div[@id='root']//*[text()='Дополнительные диски']/ancestor::div[3]//following-sibling::div//button[@id='actions-menu-button' and not (.//text()='Действия')])[last()]");
    private final SelenideElement orderPricePerDay = Selenide.$x("//*[@data-testid='new-order-details-price']");
    private final SelenideElement btnProducts = Selenide.$x("//div[not(@hidden)]/a[@href='/vm/orders' and text()='Продукты']");
    private final SelenideElement progressBars = Selenide.$x("(//div[div[@role='progressbar']])[last()]");
    private final SelenideElement loadOrderPricePerDay = Selenide.$x("//*[@data-testid='new-order-details-price']/div/*");//("//*[@data-testid='new-order-details-price']/div//*[name()='path']");
    private final SelenideElement orderPricePerDayAfterOrder = Selenide.$x("//button[@title='Редактировать']/following::span[1]");
    private final SelenideElement loadOrderPricePerDayAfterOrder = Selenide.$x("//button[@title='Редактировать']/following::*[2]");
    private final SelenideElement closeModalWindowButton = Selenide.$x("//div[@role='dialog']//button[contains(.,'Закрыть')]");
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
    private final SelenideElement graphScheme = Selenide.$x("//canvas");


    public IProductPage(IProduct product) {
        if (Objects.nonNull(product.getLink()))
            open(product.getLink());
        btnGeneralInfo.shouldBe(Condition.enabled);
        product.setLink(WebDriverRunner.getWebDriver().getCurrentUrl());
        this.product = product.buildFromLink();
        topInfo = new TopInfo();
    }

    public IProductPage() {

    }

    @Step("Ожидание выполнение действия с продуктом")
    public void waitChangeStatus() {
        List<String> titles = topInfo.getValueByColumnInFirstRow("Статус").$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e ->
                        ProductStatus.isNeedWaiting(e.getAttribute("title"))), Duration.ofMillis(20000 * 1000))
                .stream().map(e -> e.getAttribute("title")).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

    @Step("Проверка выполнения последнего действия")
    public void checkLastAction() {
        btnHistory.shouldBe(Condition.enabled).click();
        History history = new History();
        checkErrorByStatus(history.lastActionStatus());
    }

    private SelenideElement getBtnAction(String header) {
        return $x("//ancestor::div[.='{}Действия']//button[.='Действия']", header);
    }


    @Step("Запуск действия '{action}' в блоке '{headerBlock}'")
    public void runActionWithoutParameters(String headerBlock, String action) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        getBtnAction(headerBlock).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Dialog dlgActions = new Dialog(action);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        dlgActions.getDialog().shouldNotBe(Condition.visible);
        Waiting.sleep(3000);
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' в блоке '{headerBlock}' с параметрами")
    public void runActionWithParameters(String headerBlock, String action, Executable executable, boolean off) throws Throwable {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        getBtnAction(headerBlock).scrollIntoView(off);
        getBtnAction(headerBlock).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
        Waiting.sleep(3000);
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' в блоке '{headerBlock}' с параметрами")
    public void runActionScrollWithParameters(String headerBlock, String action, Executable executable, boolean off) throws Throwable {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        btnAct.scrollIntoView(off);
        btnAct.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        $x("(//li[.='{}'])[last()]", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
        Waiting.sleep(3000);
    }

    @SneakyThrows
    @Step("Получение стоимости продукта на предбиллинге")
    public double convertToDblPriceOrder(String prePrice) {
        log.info("Получение стоимости продукта на предбиллинге");
        return getNumbersFromText(prePrice);
    }

    public void checkErrorByStatus(String status) {
        if (status.equals(ProductStatus.ERROR)) {
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s",
                    product, status, StateServiceSteps.GetErrorFromStateService(product.getOrderId())));
        }
    }

    private static class TopInfo extends Table {
        public TopInfo() {
            super("Защита от удаления");
        }
    }

    private static class History extends Table {
        History() {
            super("Дата запуска");
        }

        public String lastActionStatus() {
            return getValueByColumnInFirstRow("Статус").$x("descendant::*[@title]").getAttribute("title");
        }
    }

    protected class VirtualMachine extends Table {
        public static final String POWER_STATUS_DELETED = "Удалено";
        public static final String POWER_STATUS_ON = "Включено";
        public static final String POWER_STATUS_OFF = "Выключено";

        public VirtualMachine() {
            super("Статус");
        }

        public VirtualMachine(String columnName) {
            super(columnName);
        }

        public VirtualMachine open() {
            btnGeneralInfo.click();
            return this;
        }

        public String getPowerStatus() {
            return getValueByColumnInFirstRow("Питание").$x("descendant::*[@title]").getAttribute("title");
        }

        public void checkPowerStatus(String status) {
            Assertions.assertEquals(status, new VirtualMachine("Имя хоста").getPowerStatus(), "Статус питания не соотвествует ожидаемому");
        }
    }


    public void isCostDayContains(String symbol) {
        Objects.requireNonNull(orderPricePerDay.getAttribute("textContent"));
    }

    //пользователь проверяет, что на вкладке История действий таблица содержит необходимые столбцы
    public void checkHeaderHistoryTable() {
        actionHistory.click();
        actionNameColumn.shouldBe(activeCnd);
        actionInitiatorColumn.shouldBe(activeCnd);
        actionCreationDateColumn.shouldBe(activeCnd);
        actionStartDateColumn.shouldBe(activeCnd);
        actionDurationColumn.shouldBe(activeCnd);
        actionStatusColumn.shouldBe(activeCnd);
        actionViewColumn.shouldBe(activeCnd);
        log.info("пользователь проверяет, что на вкладке История действий таблица содержит необходимые столбцы");
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Развертывание' со статусом 'Ошибка'
     */
    public void checkHistoryRowDeployErr() {
        actionHistory.click();
        historyRowDeployErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Перезагрузить по питанию' со статусом 'В порядке'
     */
    public void checkHistoryRowRestartByPowerOk() {
        actionHistory.click();
        historyRowRestartByPowerOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Перезагрузить по питанию' со статусом 'Ошибка'
     */
    public void checkHistoryRowRestartByPowerErr() {
        actionHistory.click();
        historyRowRestartByPowerErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Выключить' со статусом 'В порядке'"
     */
    public void checkHistoryRowTurnOffOk() {
        actionHistory.click();
        historyRowTurnOffOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Выключить' со статусом 'Ошибка'"
     */
    public void checkHistoryRowTurnOffErr() {
        actionHistory.click();
        historyRowTurnOffErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Изменить конфигурацию' со статусом 'В порядке'
     */
    public void checkHistoryRowChangeFlavorOk() {
        actionHistory.click();
        historyRowChangeFlavorOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Изменить конфигурацию' со статусом 'Ошибка'
     */
    public void checkHistoryRowChangeFlavorErr() {
        actionHistory.click();
        historyRowChangeFlavorErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Включить' со статусом 'В порядке'
     */
    public void checkHistoryRowTurnOnOk() {
        actionHistory.click();
        historyRowTurnOnOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Включить' со статусом 'Ошибка'
     */
    public void checkHistoryRowTurnOnErr() {
        actionHistory.click();
        historyRowTurnOnErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Добавить диск' со статусом 'В порядке'"
     */
    public void checkHistoryRowDiscAddOk() {
        actionHistory.click();
        historyRowDiscAddOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Добавить диск' со статусом 'Ошибка'"
     */
    public void checkHistoryRowDiscAddErr() {
        actionHistory.click();
        historyRowDiscAddErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Расширить диск' со статусом 'В порядке'"
     */
    public void checkHistoryRowMountExpandOkDisc() {
        actionHistory.click();
        historyRowMountExpandOkDisc.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Расширить диск' со статусом 'Ошибка'"
     */
    public void checkHistoryRowMountExpandErrDisc() {
        actionHistory.click();
        historyRowMountExpandErrDisc.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Отключить в ОС' со статусом 'В порядке'"
     */
    public void checkHistoryRowDiscTurnOffOk() {
        actionHistory.click();
        historyRowDiscTurnOffOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Отключить в ОС' со статусом 'Ошибка'"
     */
    public void checkHistoryRowDiscTurnOffErr() {
        actionHistory.click();
        historyRowDiscTurnOffErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Подключить в ОС' со статусом 'В порядке'"
     */
    public void checkHistoryRowDiscTurnOnOk() {
        actionHistory.click();
        historyRowDiscTurnOnOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Подключить в ОС' со статусом 'Ошибка'"
     */
    public void checkHistoryRowDiscTurnOnErr() {
        actionHistory.click();
        historyRowDiscTurnOnErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Удалить диск' со статусом 'В порядке'"
     */
    public void checkHistoryRowDiscDeleteOk() {
        actionHistory.click();
        historyRowDiscDeleteOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Удалить диск' со статусом 'Ошибка'"
     */
    public void checkHistoryRowDiscDeleteErr() {
        actionHistory.click();
        historyRowDiscDeleteErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Проверить конфигурацию' со статусом 'В порядке'"
     */
    public void checkHistoryRowCheckConfigOk() {
        actionHistory.click();
        historyRowCheckConfigOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Проверить конфигурацию' со статусом 'Ошибка'"
     */
    public void checkHistoryRowCheckConfigErr() {
        actionHistory.click();
        historyRowCheckConfigErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Выключить принудительно' со статусом 'В порядке'"
     */
    public void checkHistoryRowForceTurnOffOk() {
        actionHistory.click();
        historyRowForceTurnOffOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Выключить принудительно' со статусом 'Ошибка'"
     */
    public void checkHistoryRowForceTurnOffErr() {
        actionHistory.click();
        historyRowForceTurnOffErr.shouldNotBe(Condition.visible);
    }

    /**
     * пользователь проверяет наличие элемента "Строка 'Удалить' со статусом 'В порядке'"
     */
    public void checkHistoryRowDeletedOk() {
        actionHistory.click();
        historyRowDeletedOk.shouldBe(activeCnd);
    }

    /**
     * пользователь проверяет отсутствие элемента "Строка 'Удалить' со статусом 'Ошибка'"
     */
    public void checkHistoryRowDeletedErr() {
        actionHistory.click();
        historyRowDeletedErr.shouldNotBe(Condition.visible);
    }

    /**
     * проверка поля "Заказать" на форме заказа продукта до заполнения полей
     */
    public void checkFieldUntilOrder() {
        orderProduct.shouldBe(Condition.disabled);
    }

    /**
     * Проверка поля с входящими и ожидаемыми значениями
     */
    public void autoChangeableFieldCheck(SelenideElement sElement, String input, String value) {
        sElement.click();
        sElement.sendKeys(CONTROL + "a");
        sElement.sendKeys(BACK_SPACE);
        sElement.setValue(input);
        Objects.requireNonNull(sElement.getAttribute("valueAsNumber")).contains(value);
        sElement.sendKeys(CONTROL + "a");
        sElement.sendKeys(BACK_SPACE);
        log.debug("Проверка поля с входящими и ожидаемыми значениями");
    }

    public void checkFieldVmNumber() {
        autoChangeableFieldCheck(getVmNumber(), "0", "10");
        autoChangeableFieldCheck(getVmNumber(), "100", "30");
        autoChangeableFieldCheck(getVmNumber(), "N", "10");
        log.info("Проверка поля количество VM");
    }


    /**
     * пользователь проверяет детали заказа у продукта
     *
     * @param sElement
     * @param product
     */
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

    public void checkCurrentPriceEqualToPreprice() {


    }

    public static Double getNumbersFromText(String inputStr) throws ParseException {
        String numbersRegex = "\\d{1,5}.\\d{1,5}"; //(323,98 ₽/сут.), 323,98 ₽/сут.
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
        Number parsedNumber = 0;

        Pattern folderPattern = Pattern.compile(numbersRegex);
        Matcher folderMatcher = folderPattern.matcher(inputStr);

        while (folderMatcher.find()) {
            parsedNumber = numberFormat.parse(folderMatcher.group());
        }
        Double doubleDecimalObj = new Double(String.valueOf(parsedNumber));
        System.out.println(doubleDecimalObj);
        return doubleDecimalObj;
    }

    /**
     * пользователь проверяет, что текущая стоимость продукта (больше|меньше|равна) стоимости после изменения
     *
     * @param currentCost
     * @param costAfterChange
     * @param isCompare
     */
    public void vmOrderTextCompareByKey(Double currentCost, Double costAfterChange, String isCompare) {
        switch (isCompare) {
            case "больше":
                Assertions.assertTrue(currentCost > costAfterChange);
                log.info("OK! Текущая стоимость продукта " + currentCost + " больше стоимости продукта, после изменения" + costAfterChange);
                break;
            case "меньше":
                Assertions.assertTrue(currentCost < costAfterChange);
                log.info("OK! Текущая стоимость продукта " + currentCost + " меньше стоимости продукта, после изменения" + costAfterChange);
                break;
            case "равна":
                Assertions.assertEquals(currentCost, costAfterChange);
                log.info("OK! Текущая стоимость продукта " + currentCost + " равна стоимости продукта, после изменения" + costAfterChange);
                break;
        }
    }

    /**
     * Получение текущей стоимости
     */
    @SneakyThrows
    public double getCurrentCostOrder() {
        btnGeneralInfo.shouldBe(Condition.enabled);
        loadOrderPricePerDayAfterOrder.shouldBe(Condition.visible);
        loadOrderPricePerDayAfterOrder.shouldBe(clickableCnd);
        getOrderPricePerDayAfterOrder().shouldBe(activeCnd);
        String priceStr = getOrderPricePerDayAfterOrder().getAttribute("textContent");
        return getNumbersFromText(priceStr);
    }

    @SneakyThrows
    public double getCurrentCostReloadPage(models.orderService.products.Windows product) {
        double currentCost;
        do {
            new WindowsPage(product);
            currentCost = getCurrentCostOrder();
        } while (currentCost <= 0.0);

        return currentCost;
    }


    /**
     * Получение стоимости после выполнения действий над продуктом
     */
    @SneakyThrows
    public double getCostAfterChangeOrder() {
        btnGeneralInfo.shouldBe(Condition.enabled);
        loadOrderPricePerDayAfterOrder.shouldBe(Condition.visible);
        loadOrderPricePerDayAfterOrder.shouldBe(clickableCnd);
        getOrderPricePerDayAfterOrder().shouldBe(activeCnd);
        String priceStr = getOrderPricePerDayAfterOrder().getAttribute("textContent");
        return getNumbersFromText(priceStr);
    }

    @SneakyThrows
    public double getCostAfterChangeReloadPage(models.orderService.products.Windows product) {
        double costAfterChange;
        do {
            new WindowsPage(product);
            costAfterChange = getCostAfterChangeOrder();
        } while (costAfterChange <= 0.0);
        return costAfterChange;
    }

}
