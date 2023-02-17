package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;

public class BaseListPage {

    protected static final Button addNewObjectButton = Button.byXpath("//div[@data-testid = 'add-button']//button");
    protected static final SelenideElement importButton = $x("//input[@placeholder='Поиск']/following::button[1]");
    protected static final SelenideElement nextPageButton = $x("//span[@title='Вперед']/button");
    private static final SelenideElement lastPageButton = $x("//span[@title='В конец']/button");
    private static final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private static final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");
    protected final Button nextPageButtonV2 = Button.byAriaLabel("Следующая страница, выбрать");
    protected final SelenideElement sortByCreateDate = $x("//div[text()='Дата создания']");
    protected final Button createButton = Button.byText("Создать");
    protected final Button saveButton = Button.byText("Сохранить");
    protected final Button cancelButton = Button.byText("Отмена");
    protected final Button backButton = Button.byText("Назад");
    protected final Button applyFiltersButton = Button.byText("Применить");
    protected final Button clearFiltersButton = Button.byText("Сбросить фильтры");
    protected final Select graphSelect = Select.byLabel("Граф");
    protected final Select graphVersionSelect = Select.byLabel("Значение");
    protected final Input searchInput = Input.byPlaceholder("Поиск");
    private final Select recordsPerPageDropDown = Select.byXpath("//div[text()='Записей на странице:']");

    @Step("Проверка строковой сортировки по столбцу '{header}'")
    public static void checkSortingByStringField(String header) {
        Table table = new Table(header);
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.scrollIntoView(false).click();
        Waiting.sleep(1500);
        arrowIcon.shouldBe(Condition.visible);
        String firstValue = table.getValueByColumnInFirstRow(header).getText();
        String lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) > 0 || lastValue.equals(firstValue),
                "Некорректная сортировка по столбцу " + header);
        columnHeader.click();
        Waiting.sleep(1500);
        arrowIcon.shouldBe(Condition.visible);
        firstValue = table.getValueByColumnInFirstRow(header).getText();
        lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) < 0 || lastValue.equals(firstValue),
                "Некорректная сортировка по столбцу " + header);
    }

    @Step("Проверка сортировки по дате по столбцу '{header}'")
    public static void checkSortingByDateField(String header) {
        Table table = new Table(header);
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.click();
        Waiting.sleep(1500);
        arrowIcon.shouldBe(Condition.visible);
        String firstDateString = table.getValueByColumnInFirstRow(header).getText();
        String lastDateString = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'\n'HH:mm");
        LocalDateTime firstDate = LocalDateTime.parse(firstDateString, formatter);
        LocalDateTime lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isAfter(firstDate) || lastDate.isEqual(firstDate));
        columnHeader.click();
        Waiting.sleep(1500);
        arrowIcon.shouldBe(Condition.visible);
        firstDateString = table.getValueByColumnInFirstRow(header).getText();
        lastDateString = table.getValueByColumnInRow(table.getRows().size() - 1, header).getText();
        firstDate = LocalDateTime.parse(firstDateString, formatter);
        lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isBefore(firstDate) || lastDate.isEqual(firstDate));
    }

    @Step("Раскрытие меню действий для строки, содержащей в столбце '{columnName}' значение '{value}'")
    public static void openActionMenu(String columnName, String value) {
        new Table(columnName).getRowByColumnValue(columnName, value).get().$x(".//button[@id = 'actions-menu-button']")
                .click();
        TestUtils.wait(500);
    }

    @Step("Проверка, что строка, содержащая в столбце '{columnName}' значение '{value}', подсвечена как ранее выбранная")
    public static void checkRowIsHighlighted(String columnName, String value) {
        Table table = new Table(columnName);
        Assertions.assertTrue(table.getRowByColumnValue(columnName, value).get().$x("./td").getCssValue("color")
                .contains("196, 202, 212"));
    }

    @Step("Выполнение действия копирования для строки, содержащей в столбце '{columnName}' значение '{value}'")
    public static void copy(String columnName, String value) {
        openActionMenu(columnName, value);
        copyAction.click();
    }

    @Step("Выполнение действия удаления для строки, содержащей в столбце '{columnName}' значение '{value}'")
    public static void delete(String columnName, String value) {
        openActionMenu(columnName, value);
        deleteAction.click();
    }

    @Step("Переход на следующую страницу списка")
    public BaseListPage nextPage() {
        nextPageButton.scrollIntoView(true).click();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public BaseListPage lastPage() {
        lastPageButton.scrollIntoView(true).click();
        return this;
    }

    @Step("Переход на последнюю страницу списка для нового компонента таблицы")
    public BaseListPage lastPageV2() {
        Select pageSelect = Select.byXpath("//button[contains(@aria-label,'Страница 1 из')]");
        pageSelect.getElement().scrollIntoView(true).click();
        String lastPage = pageSelect.getOptions().last().getText();
        pageSelect.getElement().click();
        pageSelect.set(lastPage);
        return this;
    }

    @Step("Проверка номера страницы '{number}'")
    public BaseListPage checkPageNumber(int number) {
        $x("//button[contains(@aria-label, 'Страница {}')]", number).shouldBe(Condition.visible);
        return this;
    }

    @Step("Изменение количества отображаемых строк на '{number}'")
    public BaseListPage setRecordsPerPage(int number) {
        WebDriverRunner.getWebDriver().manage().window().maximize();
        recordsPerPageDropDown.set(Integer.toString(number));
        Assertions.assertEquals(Integer.toString(number), recordsPerPageDropDown.getElement().$x(".//span").getText());
        return this;
    }
}
