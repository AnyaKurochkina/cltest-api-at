package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$x;

public class BaseList {

    private static final SelenideElement nextPageButton = $x("//span[@title='Вперед']/button");
    private static final SelenideElement lastPageButton = $x("//span[@title='В конец']/button");

    public static void checkSortingByStringField(String header) {
        Table table = new Table(header);
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstValue = table.getValueByColumnInFirstRow(header).getValue();
        String lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getValue();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) > 0);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstValue = table.getValueByColumnInFirstRow(header).getValue();
        lastValue = table.getValueByColumnInRow(table.getRows().size() - 1, header).getValue();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) < 0);
    }

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

    public static void nextPage() {
        TestUtils.scrollToTheBottom();
        nextPageButton.click();
    }

    public static void lastPage() {
        TestUtils.scrollToTheBottom();
        lastPageButton.click();
    }
}
