package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.format;

@Getter
public class EntityListPage {

    private static final SelenideElement lastPageButton = $x("//span[@title='В конец']/button");
    private static final SelenideElement copyAction = $x("//div[@role='list'][not(@aria-hidden)]//li[.='Создать копию']");
    private static final SelenideElement deleteAction = $x("//div[@role='list'][not(@aria-hidden)]//li[.='Удалить']");
    protected final SelenideElement importButton = $x("//button[.='Импорт']");
    protected final SelenideElement nextPageButton = $x("//span[@title='Вперед']/button");
    protected final Button addNewObjectButton = Button.byXpath("//div[@data-testid = 'add-button']//button");
    protected final Button nextPageButtonV2 = Button.byAriaLabel("Следующая страница, выбрать");
    protected final SelenideElement sortByCreateDate = $x("//div[text()='Дата создания']");
    protected final Button createButton = Button.byText("Создать");
    protected final Button saveButton = Button.byText("Сохранить");
    protected final Button cancelButton = Button.byText("Отмена");
    protected final Button backButton = Button.byText("Назад");
    protected final Button closeButton = Button.byText("Закрыть");
    protected final Button applyFiltersButton = Button.byText("Применить");
    protected final Button clearFiltersButton = Button.byText("Сбросить фильтры");
    protected final Select graphSelect = Select.byLabel("Граф");
    protected final Select graphVersionSelect = Select.byLabel("Значение");
    protected final Input searchInput = Input.byPlaceholder("Поиск");
    private final Select recordsPerPageSelect = Select.byXpath("//div[div[contains(text(),'строк на странице ')]]");
    private final Select recordsPerPageSelectV2 = Select.byXpath("//div[text()='Записей на странице:']");
    private final Button groupOperationsButton = Button.byText("Групповые операции");
    private final MultiSelect tagsSelect = MultiSelect.byLabel("Теги");
    private final RadioGroup tagsMatchingType = RadioGroup.byFieldsetLabel("Совпадение тегов");

    @Step("Проверка строковой сортировки по столбцу '{header}'")
    public static void checkSortingByStringField(String header) {
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.scrollIntoView(false).click();
        Table table = new Table(header);
        Waiting.sleep(1500);
        arrowIcon.shouldBe(Condition.visible);
        String firstValue = table.getValueByColumnInFirstRow(header).getText();
        String lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) > 0 || lastValue.equals(firstValue),
                "Некорректная сортировка по столбцу " + header);
        columnHeader.click();
        Waiting.sleep(1500);
        table = new Table(header);
        arrowIcon.shouldBe(Condition.visible);
        firstValue = table.getValueByColumnInFirstRow(header).getText();
        lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) < 0 || lastValue.equals(firstValue),
                format("Некорректная сортировка по столбцу '{}'. firstValue = '{}', lastValue = '{}'",
                        header, firstValue, lastValue));
    }

    @Step("Проверка сортировки по дате по столбцу '{header}'")
    public static void checkSortingByDateField(String header) {
        checkSortingByDateField(header, DateTimeFormatter.ofPattern("dd.MM.yyyy'\n'HH:mm"));
    }

    @Step("Проверка сортировки по дате по столбцу '{header}' c форматом '{formatter}'")
    public static void checkSortingByDateField(String header, DateTimeFormatter formatter) {
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.click();
        Table table = new Table(header);
        Waiting.sleep(1500);
        arrowIcon.shouldBe(Condition.visible);
        String firstDateString = table.getValueByColumnInFirstRow(header).getText();
        String lastDateString = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        LocalDateTime firstDate = LocalDateTime.parse(firstDateString, formatter);
        LocalDateTime lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isAfter(firstDate) || lastDate.isEqual(firstDate));
        columnHeader.click();
        Waiting.sleep(1500);
        table = new Table(header);
        arrowIcon.shouldBe(Condition.visible);
        firstDateString = table.getValueByColumnInFirstRow(header).getText();
        lastDateString = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        firstDate = LocalDateTime.parse(firstDateString, formatter);
        lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isBefore(firstDate) || lastDate.isEqual(firstDate),
                format("Некорректная сортировка по столбцу '{}'. firstDate = '{}', lastDate = '{}'",
                        header, firstDate, lastDate));
    }

    @Step("Раскрытие меню действий для строки, содержащей в столбце '{columnName}' значение '{value}'")
    public static void openActionMenu(String columnName, String value) {
        DataTable table = new DataTable(columnName);
        table.searchAllPages(t -> table.isColumnValueEquals(columnName, value))
                .getRowByColumnValue(columnName, value).get()
                .$x(".//button[@id = 'actions-menu-button']")
                .click();
        Waiting.sleep(500);
    }

    @Step("Проверка, что строка, содержащая в столбце '{columnName}' значение '{value}', подсвечена как ранее выбранная")
    public static void checkRowIsHighlighted(String columnName, String value) {
        Table table = new Table(columnName);
        Assertions.assertTrue(table.getRowByColumnValue(columnName, value).get().$x("./td").getCssValue("color")
                .contains("176, 181, 189"));
    }

    @Step("Выполнение действия копирования для строки, содержащей в столбце '{columnName}' значение '{value}'")
    public static void copy(String columnName, String value) {
        openActionMenu(columnName, value);
        copyAction.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }

    @Step("Выполнение действия удаления для строки, содержащей в столбце '{columnName}' значение '{value}'")
    public static void delete(String columnName, String value) {
        openActionMenu(columnName, value);
        deleteAction.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }

    @Step("Переход на следующую страницу списка")
    public EntityListPage nextPage() {
        nextPageButton.scrollIntoView(true).click();
        return this;
    }

    @Step("Переход на следующую страницу списка")
    public EntityListPage nextPageV2() {
        nextPageButtonV2.getButton().scrollIntoView(true).click();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public EntityListPage lastPage() {
        lastPageButton.scrollIntoView(true).click();
        return this;
    }

    @Step("Переход на последнюю страницу списка для нового компонента таблицы")
    public EntityListPage lastPageV2() {
        Select pageSelect = Select.byXpath("//button[contains(@aria-label,'Страница 1 из')]");
        pageSelect.getElement().scrollIntoView(true).click();
        String lastPage = pageSelect.getOptions().last().getText();
        pageSelect.getElement().click();
        pageSelect.set(lastPage);
        return this;
    }

    @Step("Проверка номера страницы '{number}'")
    public EntityListPage checkPageNumber(int number) {
        $x("//button[contains(@aria-label, 'Страница {}')]", number).shouldBe(Condition.visible);
        return this;
    }

    @Step("Изменение количества отображаемых строк на '{number}'")
    public EntityListPage setRecordsPerPage(int number) {
        String numberString = String.valueOf(number);
        WebDriverRunner.getWebDriver().manage().window().maximize();
        if (recordsPerPageSelect.getElement().exists()) {
            recordsPerPageSelect.set(numberString);
            Waiting.find(() -> recordsPerPageSelect.getValue().contains(numberString), Duration.ofSeconds(3));
        } else {
            recordsPerPageSelectV2.set(numberString);
            Assertions.assertEquals(numberString, recordsPerPageSelectV2.getElement().$x(".//span").getText());
        }
        return this;
    }

    @Step("Проверка пагинации списка объектов")
    public EntityListPage checkPagination() {
        String allRecords = $x("//div[contains(text(),'записей из')]").getText();
        int allRecordsCount = Integer.valueOf(allRecords.substring(allRecords.lastIndexOf(" ") + 1));
        if (allRecordsCount > 10) {
            nextPageButtonV2.click();
            checkPageNumber(2);
        }
        setRecordsPerPage(25);
        setRecordsPerPage(50);
        return this;
    }

    @Step("Задание в строке поиска значения '{value}'")
    public void search(String value) {
        searchInput.setValue(value);
        Waiting.sleep(1000);
    }

    @Step("Импорт объекта из файла '{path}'")
    public EntityListPage importObject(String path) {
        importButton.click();
        new FileImportDialog(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }

    @Step("Задание фильтра по тегам")
    public EntityListPage setTagsFilter(String... values) {
        tagsSelect.set(values);
        return this;
    }

    @Step("Применение фильтров")
    public EntityListPage applyFilters() {
        applyFiltersButton.click();
        return this;
    }
}
