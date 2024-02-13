package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.enums.AuditChangeType;
import ui.elements.*;
import ui.t1.tests.audit.AuditPeriod;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static core.helper.StringUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class MeccanoAuditPage extends EntityPage {

    private final Select periodSelect = Select.byLabel("Период");
    private final Input beginDateInput = Input.byLabelV2("Начало");
    private final Input endDateInput = Input.byLabelV2("Окончание");
    private final Select beginTimeSelect = Select.byXpath("(//input[@placeholder='Время']/parent::div)[1]");
    private final Select endTimeSelect = Select.byXpath("(//input[@placeholder='Время']/parent::div)[2]");
    private final Select objectTypeSelect = Select.byLabel("Тип объекта");
    private final Input objectNameInput = Input.byLabelV2("Код объекта");
    private final Button applyFiltersButton = Button.byText("Применить");
    private final Input userFilterInput = Input.byLabelV2("Пользователь");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final Button findButton = Button.byText("Найти");
    private final SelenideElement setTypeAndCodeWarning = $x("//div[@role='alert']//div[text()='Необходимо выбрать тип и ввести код объекта']");
    private final Input auditIdInput = Input.byLabelV2("Id изменений через запятую");

    public MeccanoAuditPage() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    @Step("Проверка первой записи в таблице аудита")
    public MeccanoAuditPage checkFirstRecord(String userEmail, AuditChangeType changeType, String version) {
        Table table = new Table("Пользователь");
        table.getValueByColumnInFirstRow("Пользователь").shouldHave(Condition.text(userEmail));
        table.getValueByColumnInFirstRow("Дата изменения").shouldHave(Condition.text(LocalDateTime.now().format(formatter)));
        table.getValueByColumnInFirstRow("Тип изменения").shouldHave(Condition.exactText(changeType.getValue()));
        table.getValueByColumnInFirstRow("Версия").shouldHave(Condition.exactText(version));
        return this;
    }

    @Step("Проверка наличия записей аудита")
    public MeccanoAuditPage checkRecordsFound() {
        Table table = new Table("Пользователь");
        Waiting.find(() -> !table.isEmpty(), Duration.ofSeconds(3), "Таблица аудита не пустая");
        return this;
    }

    @Step("Проверка отсутствия записей аудита")
    public MeccanoAuditPage checkRecordsNotFound() {
        Table table = new Table("Пользователь");
        Waiting.find(() -> table.isEmpty(), Duration.ofSeconds(3), "Таблица аудита пустая");
        return this;
    }

    @Step("Проверка детальных сведений первой записи в таблице аудита")
    public MeccanoAuditPage checkFirstRecordDetails(String version, AuditChangeType changeType, String email, String objectId) {
        Table table = new Table("Пользователь");
        SelenideElement expandRowButton = table.getRow(0).get().$x(".//button");
        expandRowButton.click();
        $x("//span[.='Версия:']/..").shouldHave(Condition.text(version));
        $x("//span[.='Дата создания:']/..").shouldHave(Condition.text(LocalDateTime.now().format(formatter)));
        $x("//span[.='Дата изменения:']/..").shouldHave(Condition.text(LocalDateTime.now().format(formatter)));
        $x("//span[.='Тип изменения:']/..").shouldHave(Condition.text(changeType.getValue()));
        $x("//span[.='Email:']/..").shouldHave(Condition.text(email));
        $x("//span[.='Id объекта:']/..").shouldHave(Condition.text(objectId));
        expandRowButton.click();
        return this;
    }

    @Step("Проверка копирования значений в буфер обмена")
    public MeccanoAuditPage checkCopyToClipBoard(String value) {
        Table table = new Table("Пользователь");
        SelenideElement expandRowButton = table.getRow(0).get().$x(".//button");
        expandRowButton.click();
        $x("//div[text()='{}']/..//*[name()='svg']", value).click();
        assertEquals(getClipBoardText(), value, "Значение скопировано в буфер обмена");
        expandRowButton.click();
        return this;
    }

    @Step("Задание в фильтре по пользователю значения '{value}'")
    public MeccanoAuditPage setUserFilterAndApply(String value) {
        userFilterInput.setValue(value);
        applyFiltersButton.click();
        return this;
    }

    @Step("Очистка фильтра по пользователю")
    public MeccanoAuditPage clearUserFilter() {
        userFilterInput.getInput().$x("..//*[name()='svg']").click();
        return this;
    }

    @Step("Задание фильтра по датам")
    public MeccanoAuditPage setFilterByDate(String beginDate, String endDate) {
        periodSelect.getElement().click();
        $x("//span[text()='задать период']").click();
        beginDateInput.setValue(beginDate);
        beginTimeSelect.set("00:00");
        endDateInput.setValue(endDate);
        endTimeSelect.set("23:30");
        applyFiltersButton.click();
        return this;
    }

    @Step("Выбор периода времени")
    public MeccanoAuditPage selectPeriod(AuditPeriod periodName) {
        periodSelect.set(periodName.getUiValue());
        return this;
    }

    @Step("Проверка заголовков таблицы аудита")
    public MeccanoAuditPage checkHeaders() {
        Table table = new Table("Пользователь");
        AssertUtils.assertHeaders(table,
                "Пользователь", "Дата изменения", "Тип изменения", "Версия", "");
        return this;
    }

    @Step("Проверка, что дифф содержит значения '{oldValues}', '{newValues}'")
    public MeccanoAuditPage checkDiffContains(List<String> oldValues, List<String> newValues) {
        for (String value : oldValues) {
            $x("//div[@class='editor original']//span[text()='\"{}\"']", value).shouldBe(Condition.visible);
        }
        for (String value : newValues) {
            $x("//div[@class='editor modified']//span[text()='\"{}\"']", value).shouldBe(Condition.visible);
        }
        return this;
    }

    @Step("Выбор типа объекта")
    public MeccanoAuditPage setObjectType(String value) {
        objectTypeSelect.set(value);
        return this;
    }

    @Step("Задание кода объекта '{value}'")
    public MeccanoAuditPage setObjectName(String value) {
        objectNameInput.setValue(value);
        return this;
    }

    @Step("Задание Id изменения '{value}'")
    public MeccanoAuditPage setAuditId(String value) {
        auditIdInput.setValue(value);
        return this;
    }

    @Step("Поиск")
    public MeccanoAuditPage find() {
        findButton.click();
        return this;
    }
}
