package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.productCatalog.TestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$x;

public class BaseSteps {

    private static final SelenideElement id = $x("//form//p//b");
    private static final SelenideElement idInput = $x("//input[@name = 'id']");
    private static final SelenideElement confirmDeleteButton = $x("//form//span[text()='Удалить']/parent::button");

    public static void confirmDelete() {
        idInput.setValue(id.getText());
        confirmDeleteButton.click();
    }

    public static void checkSortingByStringField(String header, int columnNumber) {
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstValue = $x("//tbody//tr[1]//td[" + columnNumber + "]").getValue();
        String lastValue = $x("//tbody//tr[last()]//td[" + columnNumber + "]").getValue();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) > 0);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstValue = $x("//tbody//tr[1]//td[" + columnNumber + "]").getValue();
        lastValue = $x("//tbody//tr[last()]//td[" + columnNumber + "]").getValue();
        Assertions.assertTrue(lastValue.compareToIgnoreCase(firstValue) < 0);
    }

    public static void checkSortingByDateField(String header, int columnNumber) {
        SelenideElement columnHeader = $x("//div[text()='" + header + "']/parent::div");
        SelenideElement arrowIcon = $x("//div[text()='" + header + "']/following-sibling::*[name()='svg']");
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstDateString = $x("//tbody//tr[1]//td[" + columnNumber + "]").getValue();
        String lastDateString = $x("//tbody//tr[last()]//td[" + columnNumber + "]").getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSSxxx");
        LocalDateTime firstDate = LocalDateTime.parse(firstDateString, formatter);
        LocalDateTime lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isAfter(firstDate) || lastDate.isEqual(firstDate));
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstDateString = $x("//tbody//tr[1]//td[" + columnNumber + "]").getValue();
        lastDateString = $x("//tbody//tr[last()]//td[" + columnNumber + "]").getValue();
        firstDate = LocalDateTime.parse(firstDateString, formatter);
        lastDate = LocalDateTime.parse(lastDateString, formatter);
        Assertions.assertTrue(lastDate.isBefore(firstDate) || lastDate.isEqual(firstDate));
    }
}
