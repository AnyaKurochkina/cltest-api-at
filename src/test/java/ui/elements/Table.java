package ui.elements;


import com.codeborne.selenide.*;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$$x;
import static core.helper.StringUtils.$x;

public class Table implements TypifiedElement {
    @Getter
    List<String> headers;
    SelenideElement table;
    ElementsCollection rows;
    ElementsCollection headersCollection;
    //    ElementsCollection progressBars = $$x("div[div[@role='progressbar']]"); //(//div[div[@role='progressbar']])[last()]
    @Getter
    ElementsCollection progressBars = $$x("//div[contains(@style,'background-color: rgba(') and contains(@style,', 0.7)')]");

    protected void open() {
    }

    public Table(String columnName) {
        open();
//        for (SelenideElement e : progressBars)
//            waitLoadTable(e, table);
        table = $x("//table[thead/tr/th[.='{}']]", columnName).shouldBe(Condition.visible);

        $x("//div[contains(@style,'background-color: rgba(') and contains(@style,', 0.7)')]").shouldNot(Condition.exist);
        $x("//table[contains(.,'Идет обработка данных')]").shouldNot(Condition.exist);

        headersCollection = table.$$x("thead/tr/th");
        rows = table.$$x("tbody/tr");
        headersCollection.shouldBe(CollectionCondition.allMatch("Table is loaded", WebElement::isDisplayed));
        headers = headersCollection.shouldBe(CollectionCondition.allMatch("", WebElement::isDisplayed)).texts();
    }

//    public static Table getTableByColumnName(String columnName) {
//        return new Table(columnName);
//    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public SelenideElement getRowElementByColumnValue(String column, String value) {
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getIndexHeader(column)).getText().equals(value))
                return e;
        }
        throw new NotFoundException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public Row getRowByColumnValue(String column, String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).$$x("td").get(getIndexHeader(column)).getText().equals(value))
                return new Row(i);
        }
        throw new NotFoundException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    @Step("Проверка существования в колонке '{column}' значения '{value}'")
    public boolean isColumnValueExist(String column, String value) {
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getIndexHeader(column)).getText().equals(value))
                return true;
        }
        return false;
    }

    /**
     * Возвращает индекс заголовка таблицы
     * @return int
     */
    public int getIndexHeader(String column){
        int index = headers.indexOf(column);
        Assertions.assertNotEquals(-1, index, String.format("Колонка %s не найдена. Колонки: %s", column, StringUtils.join(headers, ",")));
        return index;
    }

    @AllArgsConstructor
    public class Row{
        int row;

        public String getValueByColumn(String column){
            return getValueByColumnInRow(row, column).getText();
        }
    }

//    private void waitLoadTable(SelenideElement webElement, SelenideElement table) {
//        int width;
//        int height;
//        Rectangle p1;
//        Rectangle p2;
//        do {
//            if (!webElement.exists())
//                return;
//            Waiting.sleep(200);
//            try {
//                p1 = webElement.toWebElement().getRect();
//                p2 = table.toWebElement().getRect();
//            } catch (NoSuchElementException | StaleElementReferenceException e) {
//                return;
//            }
//            int left = Math.max(p1.x, p2.x);
//            int top = Math.min(p1.y, p2.y);
//            int right = Math.min(p1.x + p1.width, p2.x + p2.width);
//            int bottom = Math.max(p1.y - p1.height, p2.y - p2.height);
//            width = right - left;
//            height = top - bottom;
//        }
//        while (((width * height) * 100 / (p2.width * p2.height)) > 50);
//    }

    public String getFirstValueByColumn(String column) {
        return getValueByColumnInFirstRow(column).getText();
    }

    @Step("Получение значения по колонке '{column}' в строке {rowIndex}")
    public SelenideElement getValueByColumnInRow(int rowIndex, String column) {
        SelenideElement row;
        try {
            row = rows.get(rowIndex);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NotFoundException("В таблице не найдены строки");
        }
        SelenideElement element;
        try {
            element = row.$$x("td").get(getIndexHeader(column));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NotFoundException(String.format("Нет колонки с индексом %d. Всего колонок %d", getIndexHeader(column), row.$$x("td").size()), e);
        }
        return element.shouldBe(Condition.visible);
    }

    public SelenideElement getValueByColumnInFirstRow(String column) {
        return getValueByColumnInRow(0, column);
    }

}
