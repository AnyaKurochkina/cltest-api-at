package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$x;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.CONTROL;
import static tests.Tests.activeCnd;

@Getter
@Log4j2
public class CommonChecks {

    private final SelenideElement orderPricePerDay = $x("//*[@data-testid='new-order-details-price']");
    private final SelenideElement btnGeneralInfo = StringUtils.$x("//button[.='Общая информация']");
    private final SelenideElement loadOrderPricePerDay = StringUtils.$x("//*[@data-testid='new-order-details-price']/div//*[1]");//("//*[@data-testid='new-order-details-price']/div//*[name()='path']");
    private final SelenideElement orderPricePerDayAfterOrder = $x("//button[@title='Редактировать']/following::span[1]");
    private final SelenideElement closeModalWindowButton = $x("//div[@role='dialog']//button[contains(.,'Закрыть')]");
    private final SelenideElement orderBtn = $x("//button[.='Заказать']");
    private final SelenideElement actionHistory = $x("//*[text()='История действий']/ancestor::button");
    private final SelenideElement actionNameColumn = $x("//table//tr/th[text()='Наименование']");
    private final SelenideElement actionInitiatorColumn = $x("//table//tr/th[text()='Инициатор']");
    private final SelenideElement actionCreationDateColumn = $x("//table//tr/th[text()='Дата создания']");
    private final SelenideElement actionStartDateColumn = $x("//table//tr/th[text()='Дата запуска']");
    private final SelenideElement actionDurationColumn = $x("//table//tr/th[text()='Продолжительность, сек']");
    private final SelenideElement actionStatusColumn = $x("//table//tr/th[text()='Статус']");
    private final SelenideElement actionViewColumn = $x("//table//tr/th[text()='Просмотр']");
    private final SelenideElement historyRowDeployOk = $x("(//td[text()='Развертывание']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDeployErr = $x("(//td[text()='Развертывание']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowRestartByPowerOk = $x("(//td[text()='Перезагрузить по питанию']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowRestartByPowerErr = $x("(//td[text()='Перезагрузить по питанию']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowTurnOffOk = $x("(//td[text()='Выключить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowTurnOffErr = $x("(//td[text()='Выключить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowChangeFlavorOk = $x("(//td[text()='Изменить конфигурацию']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowChangeFlavorErr = $x("(//td[text()='Изменить конфигурацию']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowTurnOnOk = $x("(//td[text()='Включить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowTurnOnErr = $x("(//td[text()='Включить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscAddOk = $x("(//td[text()='Добавить диск']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscAddErr = $x("(//td[text()='Добавить диск']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowMountExpandOkDisc = $x("(//td[text()='Расширить диск']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowMountExpandErrDisc = $x("(//td[text()='Расширить диск']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscTurnOffOk = $x("(//td[text()='Отключить в ОС']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscTurnOffErr = $x("(//td[text()='Отключить в ОС']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscTurnOnOk = $x("(//td[text()='Подключить в ОС']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscTurnOnErr = $x("(//td[text()='Подключить в ОС']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDiscDeleteOk = $x("(//td[text()='Удалить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDiscDeleteErr = $x("(//td[text()='Удалить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowCheckConfigOk = $x("(//td[text()='Проверить конфигурацию']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowCheckConfigErr = $x("(//td[text()='Проверить конфигурацию']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowForceTurnOffOk = $x("(//td[text()='Выключить принудительно']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowForceTurnOffErr = $x("(//td[text()='Выключить принудительно']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement historyRowDeletedOk = $x("(//td[text()='Удалить']/following-sibling::*//*[@title='В порядке'])[1]");
    private final SelenideElement historyRowDeletedErr = $x("(//td[text()='Удалить']/following-sibling::*//*[@title='Ошибка'])[1]");
    private final SelenideElement orderProduct = $x("//*[text()='Заказать']/ancestor::button");
    private final SelenideElement vmNumber = $x("//*[contains(.,'Количество')]/following-sibling::*/input");
    private final SelenideElement mark = $x("(//*[text()='Метка']//following::input)[1]");
    private final SelenideElement calculationDetails = $x("//*[text()='детализация расчета']/*");
    private final SelenideElement opMemory = $x("//div[contains(text(),'Оперативная память')]");
    private final SelenideElement hardDrive = $x("//div[contains(text(),'Жесткий диск')]");
    private final SelenideElement processor = $x("//div[contains(text(),'Процессор')]");
    private final SelenideElement windowsOS = $x("//div[contains(text(),' ОС Windows для среды DEV OpenStack')]");
    private final SelenideElement linuxOS = $x("//div[contains(text(),'ОС linux')]");
    private final SelenideElement historyRow0 = $x("//tr[@index='0']//button[@tabindex='0'][last()]");
    private final SelenideElement graphScheme = $x("//canvas");






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
     * пользователь проверяет наличие элемента "Строка 'Развертывание' со статусом 'В порядке'
     */
    public void checkHistoryRowDeployOk() {
        actionHistory.click();
        historyRowDeployOk.shouldBe(activeCnd);
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
    public  void checkFieldVmNumber()
    {
        autoChangeableFieldCheck(getVmNumber(), "0", "10");
        autoChangeableFieldCheck(getVmNumber(), "100", "30");
        autoChangeableFieldCheck(getVmNumber(), "N", "10");
        log.info("Проверка поля количество VM");
    }


    /**
     * пользователь проверяет детали заказа у продукта
     * @param sElement
     * @param product
     */
    public void checkOrderDetails(SelenideElement sElement,String product) {
      sElement.click();
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

    public void checkCurrentPriceEqualToPreprice(){


    }
    public static Double getNumbersFromText(String inputStr) throws ParseException {
        String numbersRegex = "\\d{1,5}.\\d{1,5}"; //(323,98 ₽/сут.), 323,98 ₽/сут.
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
        Number parsedNumber=0;

        Pattern folderPattern = Pattern.compile(numbersRegex);
        Matcher folderMatcher = folderPattern.matcher(inputStr);

        while (folderMatcher.find()) {
            parsedNumber = numberFormat.parse( folderMatcher.group());
        }
        Double doubleDecimalObj = new Double(String.valueOf(parsedNumber));
        System.out.println(doubleDecimalObj);
        return doubleDecimalObj;
    }



}
