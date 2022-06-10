package ui.elements;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$$x;
import static core.helper.StringUtils.$x;

public class Table implements TypifiedElement {
    final List<String> headers;
    ElementsCollection rows;
    ElementsCollection headersCollection;
    ElementsCollection progressBars = $$x("(//div[div[@role='progressbar']])[last()]");////div[div[@role='progressbar']]

    public Table(String columnName) {
        SelenideElement table = $x("//table[thead/tr/th[.='{}']]", columnName);
        headersCollection = table.$$x("thead/tr/th");
        rows = table.$$x("tbody/tr");
        headersCollection.shouldBe(CollectionCondition.allMatch("Table is loaded", WebElement::isDisplayed));
        for (SelenideElement e : progressBars)
            waitLoadTable(e, table);
        headers = headersCollection.shouldBe(CollectionCondition.sizeNotEqual(0)).texts();
    }

    public static Table getTableByColumnName(String columnName) {
        return new Table(columnName);
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public SelenideElement getRowByColumn(String column, String value) {
        int index = headers.indexOf(column);
        if (index < 0)
            throw new NoSuchElementException("Колонки " + column + " не существует ");
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(index).getText().equals(value))
                return e;
        }
        return null;
    }

    private void waitLoadTable(SelenideElement webElement, SelenideElement table) {
        int width;
        int height;
        Rectangle p1;
        Rectangle p2;
        do {
            if (!webElement.exists())
                return;
            Waiting.sleep(200);
            try {
                p1 = webElement.toWebElement().getRect();
                p2 = table.toWebElement().getRect();
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return;
            }
            int left = Math.max(p1.x, p2.x);
            int top = Math.min(p1.y, p2.y);
            int right = Math.min(p1.x + p1.width, p2.x + p2.width);
            int bottom = Math.max(p1.y - p1.height, p2.y - p2.height);
            width = right - left;
            height = top - bottom;
        }
        while (((width * height) * 100 / (p2.width * p2.height)) > 50);
    }

    public String getFirstValueByColumn(String column) {
        return getValueByColumnInFirstRow(column).getText();
    }

    @Step("Получение значения по колонке '{column}' в первой строке'")
    public SelenideElement getValueByColumnInFirstRow(String column) {
        int index = headers.indexOf(column);
        return rows.get(0).$$x("td").get(index);
    }

}
