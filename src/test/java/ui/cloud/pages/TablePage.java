package ui.cloud.pages;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import core.utils.Waiting;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class TablePage {
    final List<String> headers;

    ElementsCollection rows;
    ElementsCollection headersCollection;
    ElementsCollection progressBars = $$x("//div[div[@role='progressbar']]");

    public TablePage(String columnName) {
        SelenideElement table = $x(String.format("//table[thead/tr/th[.='%s']]", columnName));
        headersCollection = table.$$x("thead/tr/th");
        rows = table.$$x("tbody/tr");
        headersCollection.shouldBe(CollectionCondition.allMatch("Table is loaded", WebElement::isDisplayed));
        for (SelenideElement e : progressBars)
            freeTable(e, columnName);
        headers = headersCollection.shouldBe(CollectionCondition.sizeNotEqual(0)).texts();
    }

    public SelenideElement getRowByColumn(String column, String value) {
        int index = headers.indexOf(column);
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(index).getText().equals(value))
                return e;
        }
        return null;
    }

    private void freeTable(SelenideElement webElement, String columnName) {
        int width;
        int height;
        Rectangle p1;
        Rectangle p2;
        do {
            if (!webElement.exists())
                return;
            Waiting.sleep(200);
            try {
                p1 = webElement.shouldBe(Condition.visible, Duration.ZERO).getRect();
                p2 = $x(String.format("//table[thead/tr/th[.='%s']]", columnName)).shouldBe(Condition.visible, Duration.ZERO).getRect();
            } catch (ElementShould e) {
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
        return getFirstRowByColumn(column).getText();
    }

    public SelenideElement getFirstRowByColumn(String column) {
        int index = headers.indexOf(column);
        return rows.get(0).$$x("td").get(index);
    }

}
