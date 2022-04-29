package ui.cloud.pages;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class TablePage {
    final List<String> headers;

    ElementsCollection rows;
    ElementsCollection headersCollection;
    ElementsCollection progressBars = $$x("//div[@role='progressbar']");

    public TablePage(String columnName) {
        SelenideElement table = $x(String.format("//table[thead/tr/th[.='%s']]", columnName));
        headersCollection = table.$$x("thead/tr/th");
        rows = table.$$x("tbody/tr");
        headersCollection.shouldBe(CollectionCondition.allMatch("Table is loaded", WebElement::isDisplayed));
        progressBars.shouldBe(CollectionCondition.noneMatch("ProgressBars are hidden", WebElement::isDisplayed));
        headers = headersCollection.shouldBe(CollectionCondition.sizeNotEqual(0)).texts();
    }

    public SelenideElement getRowByColumn(String column, String value){
        int index = headers.indexOf(column);
        for(SelenideElement e : rows){
            if(e.$$x("td").get(index).getText().equals(value))
                return e;
        }
        return null;
    }

    public String getFirstValueByColumn(String column){
        return getFirstRowByColumn(column).getText();
    }

    public SelenideElement getFirstRowByColumn(String column){
        int index = headers.indexOf(column);
        return rows.get(0).$$x("td").get(index);
    }

}
