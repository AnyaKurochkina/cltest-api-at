package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$x;

public class BaseList {

    private static final SelenideElement nextPageButton = $x("//span[@title='Вперед']/button");
    private static final SelenideElement lastPageButton = $x("//span[@title='В конец']/button");
    private static final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private static final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");

    @Step("Проверка строковой сортировки по столбцу '{header}'")
    public static void checkSortingByStringField(String header) {
        Table table = new Table(header);
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstValue = table.getValueByColumnInFirstRow(header).getValue();
        String lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getValue();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) > 0 || lastValue.equals(firstValue));
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstValue = table.getValueByColumnInFirstRow(header).getValue();
        lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getValue();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) < 0 || lastValue.equals(firstValue));
    }

    @Step("Проверка сортировки по дате по столбцу '{header}'")
    public static void checkSortingByDateField(String header) {
        Table table = new Table(header);
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstDateString = table.getValueByColumnInFirstRow(header).getValue();
        String lastDateString = table.getValueByColumnInRow(table.getRows().size() - 1, header).getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSSxxx");
        LocalDateTime firstDate = LocalDateTime.parse(firstDateString, formatter);
        LocalDateTime lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isAfter(firstDate) || lastDate.isEqual(firstDate));
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstDateString = table.getValueByColumnInFirstRow(header).getValue();
        lastDateString = table.getValueByColumnInRow(table.getRows().size() - 1, header).getValue();
        firstDate = LocalDateTime.parse(firstDateString, formatter);
        lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isBefore(firstDate) || lastDate.isEqual(firstDate));
    }

    @Step("Переход на следующую страницу списка")
    public static void nextPage() {
        TestUtils.scrollToTheBottom();
        nextPageButton.click();
    }

    @Step("Переход на последнюю страницу списка")
    public static void lastPage() {
        TestUtils.scrollToTheBottom();
        lastPageButton.click();
    }

    @Step("Раскрытие меню действий для строки, содержащей в столбце 'columnName' значение 'value'")
    public static void openActionMenu(String columnName, String value) {
        new Table(columnName).getRowElementByColumnValue(columnName, value).$x(".//button[@id = 'actions-menu-button']")
                .click();
        TestUtils.wait(500);
    }

    @Step("Проверка, что строка, содержащая в столбце 'columnName' значение 'value', подсвечена как ранее выбранная")
    public static void checkRowIsHighlighted(String columnName, String value) {
        Table table = new Table(columnName);
        Assertions.assertTrue(table.getRowElementByColumnValue(columnName, value)
                .getCssValue("color").contains("196, 202, 212"));
    }

    @Step("Выполнение действия копирования для строки, содержащей в столбце 'columnName' значение 'value'")
    public static void copy(String columnName, String value) {
        new Table(columnName).getRowElementByColumnValue(columnName, value).$x(".//button[@id = 'actions-menu-button']")
                .click();
        copyAction.click();
    }

    @Step("Выполнение действия удаления для строки, содержащей в столбце 'columnName' значение 'value'")
    public static void delete(String columnName, String value) {
        new Table(columnName).getRowElementByColumnValue(columnName, value).$x(".//button[@id = 'actions-menu-button']")
                .click();
        deleteAction.click();
    }
}
