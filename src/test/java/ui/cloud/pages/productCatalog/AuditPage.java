package ui.cloud.pages.productCatalog;

import api.Tests;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;
import ui.t1.tests.audit.AuditPeriod;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.getClipBoardText;
import static ui.elements.TypifiedElement.scrollCenter;

@Getter
public class AuditPage extends EntityPage {

    public static final String NO_VALUE = "—";
    private final SelenideElement contextId = $x("//span[text()='ID контекста']/following::span[1]");
    private final SelenideElement address = $x("//span[text()='Адрес']/following::span[1]");
    private final SelenideElement request = $x("//span[text()='Запрос']/ancestor::div[2]");
    private final SelenideElement response = $x("//span[text()='Ответ']/ancestor::div[2]");
    private final CheckBox showRequest = CheckBox.byName("returnLogBody");
    private final CheckBox showResponse = CheckBox.byName("returnLogReplyBody");
    private final Button copyDataButton = Button.byXpath("//span[text()='Адрес']/preceding::button[1]");
    private final Button showFullView = Button.byXpath("//span[text()='Ответ']//following::button[1]");
    private final Button copyAddressButton = Button.byXpath("//span[text()='Адрес']/following::button[1]");
    private final Button copyRequestButton = Button.byXpath("//span[text()='Запрос']/following::button[2]");
    private final Button copyResponseButton = Button.byXpath("//span[text()='Ответ']/following::button[2]");
    private final SelenideElement additionalFilters = $x("//div[text()='Дополнительные фильтры']");
    private final SelenideElement clearOperationTypeFilter = $x("//*[@id='searchSelectClearIcon']");
    private final Button applyAdditionalFiltersButton = Button.byXpath("//label[text()='Учетная запись']//following::div[text()='Применить']/parent::button");
    private final SelenideElement applyFiltersByDateButton = $x("//label[text()='Учетная запись']//preceding::div[text()='Применить']/parent::button");
    private final Button closeFullViewButton = Button.byAriaLabel("close");
    private final Select periodSelect = Select.byLabel("Период");
    private final SearchSelect operationTypeSelect = SearchSelect.byLabel("Тип операции");
    private final SearchSelect serviceFilterSelect = SearchSelect.byLabel("Сервис");
    private final Input beginDateInput = Input.byLabelV2("Начало");
    private final Input endDateInput = Input.byLabelV2("Окончание");
    private final Select beginTimeSelect = Select.byXpath("(//input[@placeholder='Время']/parent::div)[1]");
    private final Select endTimeSelect = Select.byXpath("(//input[@placeholder='Время']/parent::div)[2]");
    private final Input statusCodeInput = Input.byLabelV2("Код статуса");
    private final Input objectTypeFilterInput = Input.byLabelV2("Тип объекта");
    private final Input objectIdFilterInput = Input.byLabelV2("ID объекта");
    private final Button exportCsvButton = Button.byLabel("Экспорт в CSV");
    private final Tab auditTab = Tab.byText("История изменений");

    public AuditPage() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    @Step("[Проверка] Поля начало и окончание периода отключены для дефолтного значения последний 1 час")
    public AuditPage checkPeriodFieldsAreDisabledForDefaultSortingLastHour() {
        Assertions.assertEquals(AuditPeriod.LAST_HOUR.getUiValue(), periodSelect.getValue(), "По дефолту выбран период последний 1 час");
        beginDateInput.getInput().shouldBe(Condition.disabled.because("Поле Дата начала периода должно быть отключено"));
        endDateInput.getInput().shouldBe(Condition.disabled.because("Поле Дата окончания периода должно быть отключено"));
        return this;
    }

    @Step("Проверка содержания записи в таблице аудита")
    public AuditPage checkAuditContains(String dateTime, String user, String operationType, String object,
                                        String statusCode, String status) {
        Table table = new Table("Учетная запись");
        table.getRowByColumnValueContains("Дата и время", dateTime);
        table.getRowByColumnValueContains("Учетная запись", user);
        table.getRowByColumnValue("Тип операции", operationType);
        table.getRowByColumnValue("Объект", object);
        table.getRowByColumnValueContains("Код статуса", statusCode);
        table.getRowByColumnValue("Статус", status);
        return this;
    }

    @Step("Проверка отсутствия пользователя ")
    public AuditPage checkUserNotFound(String userName) {
        Table table = new Table("Учетная запись");
        Assertions.assertFalse(table.isColumnValueEquals("Учетная запись", userName));
        return this;
    }

    @Step("Проверка первой записи в таблице аудита")
    public AuditPage checkFirstRecord(String dateTime, String user, String operationType, String object, String statusCode, String status) {
        Waiting.sleep(1000);
        checkAuditIsLoaded();
        Table table = new Table("Учетная запись");
        if (!table.isColumnValueContains("Тип операции", operationType)) {
            Waiting.sleep(2000);
            Selenide.refresh();
            new EntityPage().goToAuditTab();
            table = new Table("Учетная запись");
        }
        table.getValueByColumnInFirstRow("Дата и время").shouldHave(Condition.text(dateTime));
        table.getValueByColumnInFirstRow("Учетная запись").shouldHave(Condition.text(user));
        table.getValueByColumnInFirstRow("Тип операции").shouldHave(Condition.exactText(operationType));
        table.getValueByColumnInFirstRow("Объект").shouldHave(Condition.exactText(object));
        table.getValueByColumnInFirstRow("Код статуса").shouldHave(Condition.text(statusCode));
        table.getValueByColumnInFirstRow("Статус").shouldHave(Condition.exactText(status));
        return this;
    }

    @Step("Проверка отображения записи с типом операции '{operationType}'")
    public AuditPage checkRecordWithOperationTypeFound(String operationType) {
        Waiting.sleep(600);
        Table table = new Table("Учетная запись");
        table.getRowByColumnValue("Тип операции", operationType);
        return this;
    }

    @Step("Проверка детальных сведений первой записи в таблице аудита")
    public AuditPage checkFirstRecordDetails(String contextId, String address, String request, String response) {
        Table table = new Table("Учетная запись");
        table.getRow(0).get().scrollIntoView(scrollCenter).click();
        this.contextId.shouldHave(Condition.exactText(contextId));
        this.address.shouldHave(Condition.text(address));
        if (showRequest.getChecked() && !request.equals(NO_VALUE)) {
            this.request.$x(".//descendant::span[2]").shouldHave(Condition.text(request));
        } else {
            this.request.$x(".//i").shouldHave(Condition.exactText(request));
        }
        if (showResponse.getChecked() && !response.equals(NO_VALUE)) {
            this.response.$x(".//descendant::span[2]").shouldHave(Condition.text(response));
        } else {
            this.response.$x(".//i").shouldHave(Condition.exactText(response));
        }
        table.getRow(0).get().click();
        return this;
    }

    @Step("Проверка детальных сведений записи с contextId '{contextId}'")
    public AuditPage checkRecordDetailsByContextId(String contextId, String address, String request, String response) {
        openRecordByContextId(contextId);
        this.contextId.shouldHave(Condition.exactText(contextId));
        this.address.shouldHave(Condition.text(address));
        if (showRequest.getChecked() && !request.equals(NO_VALUE)) {
            this.request.$x(".//descendant::span[2]").shouldHave(Condition.text(request));
        } else {
            this.request.$x(".//i").shouldHave(Condition.exactText(request));
        }
        if (showResponse.getChecked() && !response.equals(NO_VALUE)) {
            this.response.$x(".//descendant::span[2]").shouldHave(Condition.text(response));
        } else {
            this.response.$x(".//i").shouldHave(Condition.exactText(response));
        }
        return this;
    }

    @Step("Проверка детальных сведений записи, содержащей в ответе '{response}'")
    public AuditPage checkRecordDetailsByResponse(String contextId, String address, String request, String response) {
        openRecordByResponse(response);
        this.contextId.shouldHave(Condition.exactText(contextId));
        this.address.shouldHave(Condition.text(address));
        if (showRequest.getChecked() && !request.equals(NO_VALUE)) {
            this.request.$x(".//descendant::span[2]").shouldHave(Condition.text(request));
        } else {
            this.request.$x(".//i").shouldHave(Condition.exactText(request));
        }
        if (showResponse.getChecked() && !response.equals(NO_VALUE)) {
            this.response.$x(".//descendant::span[2]").shouldHave(Condition.text(response));
        } else {
            this.response.$x(".//i").shouldHave(Condition.exactText(response));
        }
        return this;
    }

    @Step("Проверка отсутствия записей в аудите")
    public AuditPage checkRecordsNotFoundV2() {
        $x("//td[text()='Нет данных для отображения']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Включение отображения запроса и ответа")
    public AuditPage showRequestAndResponse() {
        showRequest.getElement().scrollIntoView(false);
        showRequest.setChecked(true);
        showResponse.setChecked(true);
        return this;
    }

    @Step("Проверка копирования в буфер обмена")
    public AuditPage checkCopyToClipboard(String value, String contextId) {
        openRecordByContextId(contextId);
        copyDataButton.getButton().scrollIntoView(scrollCenter).click();
        Assertions.assertTrue(getClipBoardText().contains(this.contextId.getText()));
        copyAddressButton.click();
        Assertions.assertEquals(getClipBoardText(), address.getText());
        if (!request.$x(".//i").exists()) {
            copyRequestButton.click();
            Assertions.assertTrue(getClipBoardText().contains(value));
        }
        copyResponseButton.click();
        Assertions.assertTrue(getClipBoardText().contains(value));
        return this;
    }

    @Step("Проверка отображения '{value}' в полноэкранном режиме ответа")
    public AuditPage checkResponseFullViewContains(String value, String contextId) {
        openRecordByContextId(contextId);
        showFullView.getButton().scrollIntoView(scrollCenter).click();
        Waiting.sleep(2000);
        Assertions.assertTrue($x("//span[text()='\"" + value + "\"']").isDisplayed(),
                "Ответ не содержит " + value);
        closeFullViewButton.click();
        return this;
    }

    @Step("Перезагрузка страницы, если данные аудита не загрузились")
    private void checkAuditIsLoaded() {
        if ($x("//div[text()='Дата и время']/ancestor::table//td[text()='Нет данных для отображения']").exists()) {
            Waiting.sleep(2000);
            Selenide.refresh();
            if (Waiting.sleep(() -> auditTab.getElement().exists(), Duration.ofSeconds(3)))
                auditTab.switchTo();
        }
    }

    @Step("Задание фильтра по типу операции")
    public AuditPage setOperationTypeFilterAndApply(String value) {
        showAdditionalFilters();
        operationTypeSelect.set(value);
        return this;
    }

    @Step("Задание в фильтре по учетной записи значения '{value}'")
    public AuditPage setUserFilter(String value) {
        showAdditionalFilters();
        Input userInput = Input.byLabelV2("Учетная запись");
        userInput.setValue(value);
        return this;
    }

    @Step("Задание в фильтре по коду статуса значения '{value}'")
    public AuditPage setStatusCodeFilter(String value) {
        showAdditionalFilters();
        statusCodeInput.setValue(value);
        return this;
    }

    @Step("Задание в фильтре по типу объекта значения '{value}'")
    public AuditPage setObjectType(String value) {
        showAdditionalFilters();
        objectTypeFilterInput.setValue(value);
        return this;
    }

    @Step("Задание в фильтре по ID объекта значения '{value}'")
    public AuditPage setObjectIdFilter(String value) {
        showAdditionalFilters();
        objectIdFilterInput.setValue(value);
        return this;
    }

    @Step("Задание в фильтре по коду статуса значения '{value}'")
    public AuditPage setServiceFilterAndApply(String value) {
        showAdditionalFilters();
        serviceFilterSelect.set(value);
        return this;
    }

    @Step("Раскрытие дополнительных фильтров")
    private void showAdditionalFilters() {
        if (!operationTypeSelect.getElement().isDisplayed()) {
            additionalFilters.scrollIntoView(scrollCenter).click();
        }
    }

    @Step("Очистка дополнительных фильтров")
    public AuditPage clearAdditionalFilters() {
        if (clearOperationTypeFilter.exists()) {
            clearOperationTypeFilter.click();
        }
        Input.byLabelV2("Учетная запись").clear();
        Input.byLabelV2("Код статуса").clear();
        return this;
    }

    @Step("Применение фильтров")
    public AuditPage applyAdditionalFilters() {
        showAdditionalFilters();
        applyAdditionalFiltersButton.click();
        return this;
    }

    @Step("Задание фильтра по датам")
    public AuditPage setFilterByDate(String beginDate, String endDate) {
        TestUtils.scrollToTheTop();
        periodSelect.set("задать период");
        beginDateInput.setValue(beginDate);
        beginTimeSelect.set("00:00");
        endDateInput.setValue(endDate);
        endTimeSelect.set("23:30");
        applyFiltersByDateButton.click();
        return this;
    }

    @Step("Выбор периода времени")
    public AuditPage selectPeriod(AuditPeriod periodName) {
        TestUtils.scrollToTheTop();
        periodSelect.set(periodName.getUiValue());
        return this;
    }

    @Step("Проверка отображения записи с типом операции '{operationType}'")
    public AuditPage checkHeaders() {
        Table table = new Table("Учетная запись");
        AssertUtils.assertHeaders(table,
                "Дата и время", "Учетная запись", "Тип операции", "Объект", "Код статуса", "Статус", "");
        return this;
    }

    @Step("Проверка сортировки по дате и времени")
    public AuditPage checkSortingByDate() {
        EntityListPage.checkSortingByDateField("Дата и время", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        return this;
    }

    @Step("Клик по кнопке Экспортировать csv")
    public AuditPage exportCsv() {
        exportCsvButton.getButton()
                .shouldBe(Tests.activeCnd.because("Кнопка экспортировать csv должна быть видима и кликабельна"))
                .click();
        return this;
    }

    @Step("Раскрыть запись с contextId '{contextId}'")
    private void openRecordByContextId(String contextId) {
        Table table = new Table("Учетная запись");
        for (SelenideElement row : table.getRows()) {
            if (this.contextId.exists() && this.contextId.getText().equals(contextId)) break;
            row.scrollIntoView(scrollCenter).click();
        }
    }

    @Step("Раскрыть запись, содержащую в ответе '{value}'")
    private void openRecordByResponse(String value) {
        Table table = new Table("Учетная запись");
        for (SelenideElement row : table.getRows()) {
            row.scrollIntoView(scrollCenter).click();
            if (this.response.exists() && this.response.getText().contains(value)) break;
        }
    }
}
