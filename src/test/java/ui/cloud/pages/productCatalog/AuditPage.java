package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.graph.GraphPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import static com.codeborne.selenide.Selenide.$x;

public class AuditPage extends GraphPage {

    private final SelenideElement contextId = $x("//span[text()='ID контекста']/following::span[1]");
    private final SelenideElement address = $x("//span[text()='Адрес']/following::span[1]");
    private final SelenideElement request = $x("//span[text()='Запрос']/ancestor::div[2]");
    private final SelenideElement response = $x("//span[text()='Ответ']/ancestor::div[2]");
    private final SelenideElement showRequest = $x("//input[@name='returnLogBody']");
    private final SelenideElement showResponse = $x("//input[@name='returnLogReplyBody']");
    private final SelenideElement paramsFullView = $x("//div[@role='dialog']//p");
    private final SelenideElement copyContextIdButton = $x("//tbody//button[@title='Скопировать данные']");
    private final SelenideElement showFullView = $x("//span[text()='Ответ']//following::button[1]");
    private final SelenideElement copyAddressButton = $x("//span[text()='Адрес']/following::button[@title='Скопировать'][1]");
    private final SelenideElement additionalFilters = $x("//div[text()='Дополнительные фильтры']");
    private final SelenideElement clearOperationTypeFilter = $x("//*[@id='searchSelectClearIcon']");
    private final SelenideElement applyAdditionalFiltersButton = $x("//label[text()='Учетная запись']//following::div[text()='Применить']/parent::button");
    private final SelenideElement applyFiltersByDateButton = $x("//label[text()='Учетная запись']//preceding::div[text()='Применить']/parent::button");
    private final SelenideElement closeFullViewButton = $x("//button[@aria-label='close']");

    @Step("Проверка первой записи в таблице аудита")
    public AuditPage checkFirstRecord(String dateTime, String user, String operationType, String object, String statusCode, String status) {
        TestUtils.wait(1000);
        checkAuditIsLoaded();
        Table table = new Table("Учетная запись");
        if (!table.isColumnValueContains("Тип операции", operationType)) {
            TestUtils.wait(2000);
            Selenide.refresh();
            new GraphPage().goToAuditTab();
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
        TestUtils.wait(600);
        Table table = new Table("Учетная запись");
        table.getRowElementByColumnValue("Тип операции", operationType);
        return this;
    }

    @Step("Проверка детальных сведений первой записи в таблице аудита")
    public AuditPage checkFirstRecordDetails(String contextId, String address, String request, String response) {
        Table table = new Table("Учетная запись");
        TestUtils.scrollToTheBottom();
        table.getRowByIndex(0).click();
        this.contextId.shouldHave(Condition.exactText(contextId));
        this.address.shouldHave(Condition.text(address));
        if (showRequest.isSelected()) {
            this.request.$x(".//descendant::span[2]").shouldHave(Condition.text(request));
        } else {
            this.request.$x(".//i").shouldHave(Condition.exactText(request));
        }
        if (showResponse.isSelected()) {
            this.response.$x(".//descendant::span[2]").shouldHave(Condition.text(request));
        } else {
            this.response.$x(".//i").shouldHave(Condition.exactText(response));
        }
        table.getRowByIndex(0).click();
        return this;
    }

    @Step("Проверка отсутствия записей в аудите")
    public AuditPage checkRecordsNotFound() {
        TestUtils.wait(600);
        Assertions.assertTrue($x("//div[text()='Дата и время']/ancestor::table//td[text()='Нет данных для отображения']")
                .exists());
        return this;
    }

    @Step("Включение отображения запроса и ответа")
    public AuditPage showRequestAndResponse() {
        showRequest.click();
        showResponse.click();
        return this;
    }

    @Step("Проверка копирования в буфер обмена")
    public AuditPage checkCopyToClipboard() {
        Table table = new Table("Учетная запись");
        table.getRowByIndex(0).click();
        copyContextIdButton.scrollTo().click();
        Assertions.assertTrue(Selenide.clipboard().getText().contains(contextId.getText()));
        copyAddressButton.click();
        Assertions.assertTrue(Selenide.clipboard().getText().equals(address.getText()));
        table.getRowByIndex(0).click();
        return this;
    }

    @Step("Проверка отображения '{value}' в полноэкранном режиме ответа")
    public AuditPage checkResponseFullViewContains(String value) {
        Table table = new Table("Учетная запись");
        table.getRowByIndex(0).click();
        TestUtils.scrollToTheBottom();
        showFullView.click();
        TestUtils.wait(500);
        Assertions.assertTrue($x("//span[text()='\"" + value + "\"']").isDisplayed());
        closeFullViewButton.click();
        return this;
    }

    @Step("Перезагрузка страницы, если данные аудита не загрузились")
    private void checkAuditIsLoaded() {
        if ($x("//div[text()='Дата и время']/ancestor::table//td[text()='Нет данных для отображения']").exists()) {
            TestUtils.wait(2000);
            TypifiedElement.refresh();
            new GraphPage().goToAuditTab();
        }
    }

    @Step("Задание фильтра по типу операции")
    public AuditPage setOperationTypeFilterAndApply(String value) {
        showAdditionalFilters();
        DropDown operationTypeDropDown = DropDown.byLabel("Тип операции");
        operationTypeDropDown.selectByTitle(value);
        return this;
    }

    @Step("Задание в фильтре по учетной записи значения '{value}'")
    public AuditPage setUserFilterAndApply(String value) {
        showAdditionalFilters();
        Input userInput = Input.byLabelV2("Учетная запись");
        userInput.setValue(value);
        applyAdditionalFiltersButton.scrollTo().click();
        return this;
    }

    @Step("Задание в фильтре по коду статуса значения '{value}'")
    public AuditPage setStatusCodeFilterAndApply(String value) {
        showAdditionalFilters();
        Input statusCodeInput = Input.byLabelV2("Код статуса");
        statusCodeInput.setValue(value);
        applyAdditionalFiltersButton.scrollTo().click();
        return this;
    }

    @Step("Раскрытие дополнительных фильтров")
    private void showAdditionalFilters() {
        TestUtils.scrollToTheTop();
        DropDown operationTypeDropDown = DropDown.byLabel("Тип операции");
        if (!operationTypeDropDown.getElement().isDisplayed()) {
            additionalFilters.click();
        }
    }

    @Step("Очистка дополнительных фильтров")
    public AuditPage clearAdditionalFilters() {
        clearOperationTypeFilter.click();
        Input.byLabelV2("Учетная запись").clear();
        Input.byLabelV2("Код статуса").clear();
        return this;
    }

    @Step("Задание фильтра по датам")
    public AuditPage setFilterByDate(String beginDate, String endDate) {
        TestUtils.scrollToTheTop();
        DropDown periodDropDown = DropDown.byLabel("Период");
        periodDropDown.selectByTitle("задать период");
        Input beginDateInput = Input.byLabelV2("Начало");
        beginDateInput.setValue(beginDate);
        Input endDateInput = Input.byLabelV2("Окончание");
        endDateInput.setValue(endDate);
        applyFiltersByDateButton.click();
        return this;
    }

    @Step("Выбор периода времени")
    public AuditPage selectPeriod(String periodName) {
        TestUtils.scrollToTheTop();
        DropDown periodDropDown = DropDown.byLabel("Период");
        periodDropDown.selectByTitle(periodName);
        return this;
    }
}
