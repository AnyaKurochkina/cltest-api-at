package ui.elements;


import com.codeborne.selenide.*;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static core.helper.StringUtils.$x;

public class Table implements TypifiedElement {
    @Getter
    List<String> headers;
    SelenideElement table;
    @Getter
    ElementsCollection rows;
    ElementsCollection headersCollection;
    @Language("XPath")
    private String xpath;

    protected void open() {}

    public Table(String columnName) {
        open();
        table = $x("//table[thead/tr/th[.='{}']]", columnName).shouldBe(Condition.visible);
        init(table);
    }

    public Table(String columnName, int index) {
        open();
        table = $x("(//table[thead/tr/th[.='{}']])" + TypifiedElement.postfix, columnName, TypifiedElement.getIndex(index)).shouldBe(Condition.visible);
        init(table);
    }

    public Table(SelenideElement table) {
        open();
        init(table);
    }

    public Table update(){
        init($x(xpath));
        return this;
    }

    public void init(SelenideElement table) {
        xpath = table.getSearchCriteria().replaceAll("By.xpath: ", "");

        $x("//div[contains(@style,'background-color: rgba(') and contains(@style,', 0.7)')]").shouldNot(Condition.exist);
        $x("//table[contains(.,'Идет обработка данных')]").shouldNot(Condition.exist);

        headersCollection = table.$$x("thead/tr/th");
        rows = table.$$x("tbody/tr[td]");
        headersCollection.shouldBe(CollectionCondition.allMatch("Table is loaded", WebElement::isDisplayed));
        headers = headersCollection.shouldBe(CollectionCondition.allMatch("", WebElement::isDisplayed)).texts();
    }

    @Deprecated
    //Use getRowByColumnValue(..).get()
    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public SelenideElement getRowElementByColumnValue(String column, String value) {
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getHeaderIndex(column)).hover().getText().equals(value))
                return e;
        }
        throw new NotFoundException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public Row getRowByColumnValue(String column, String value) {
        Row row;
        try {
            row = getRowByColumnIndex(getHeaderIndex(column), value);
        } catch (NotFoundException e) {
            throw new NotFoundException("Не найдена строка по колонке " + column + " и значению " + value);
        }
        return row;
    }

    public Row getRowByColumnIndex(int index, String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).$$x("td").get(index).hover().getText().equals(value))
                return new Row(i);
        }
        throw new NotFoundException();
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public Row getRowByColumnValueContains(String column, String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).$$x("td").get(getHeaderIndex(column)).hover().getText().contains(value))
                return new Row(i);
        }
        throw new NotFoundException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    @Step("Проверка, что в колонке '{column}' есть значение, равное '{value}'")
    public boolean isColumnValueEquals(String column, String value) {
        if(isEmpty())
            return false;
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getHeaderIndex(column)).hover().getText().equals(value))
                return true;
        }
        return false;
    }

    public boolean isEmpty() {
        if (rows.isEmpty())
            return true;
        if (rows.size() == 1)
            return rows.first().$$x("td").size() == 1 && headers.size() > 1;
        return false;
    }

    public int rowSize() {
        return rows.size();
    }


    @Step("Проверка, что в колонке '{column}' есть значение, содержащее '{value}'")
    public boolean isColumnValueContains(String column, String value) {
        if(isEmpty())
            return false;
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getHeaderIndex(column)).hover().getText().contains(value))
                return true;
        }
        return false;
    }

    @Deprecated
    //Use getRow(0).get()
    public SelenideElement getRowByIndex(int index) {
        Assertions.assertTrue(rows.size() > index, "Индекс больше кол-ва строк");
        return rows.get(index);
    }

    public Row getRow(int index) {
        Assertions.assertTrue(rows.size() > index, "Индекс больше кол-ва строк");
        return new Row(index);
    }

    /**
     * Возвращает индекс заголовка таблицы
     * @return int
     */
    public int getHeaderIndex(String column) {
        int index = headers.indexOf(column);
        Assertions.assertNotEquals(-1, index, String.format("Колонка %s не найдена. Колонки: %s", column, StringUtils.join(headers, ",")));
        return index;
    }

    @AllArgsConstructor
    public class Row {
        int row;

        public SelenideElement get(){
            return getRowByIndex(row);
        }

        public String getValueByColumn(String column) {
            return getValueByColumnInRow(row, column).hover().getText();
        }

        public SelenideElement getElementByColumn(String column) {
            return getValueByColumnInRow(row, column);
        }

        public SelenideElement getElementByColumnIndex(int column) {
            SelenideElement element;
            try {
                element = get().$$x("td").get(column);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NotFoundException("Нет колонки с индексом " + column);
            }
            return element;
        }
    }

    public String getFirstValueByColumn(String column) {
        return getValueByColumnInFirstRow(column).hover().getText();
    }

    @Step("Получение значения по колонке '{column}' в строке #{rowIndex}")
    public SelenideElement getValueByColumnInRow(int rowIndex, String column) {
        SelenideElement row = getRowByIndex(rowIndex);
        SelenideElement element;
        try {
            element = row.$$x("td").get(getHeaderIndex(column));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NotFoundException(String.format("Нет колонки с индексом %d. Всего колонок %d", getHeaderIndex(column), row.$$x("td").size()), e);
        }
        return element.shouldBe(Condition.visible);
    }

    public SelenideElement getValueByColumnInFirstRow(String column) {
        return getValueByColumnInRow(0, column);
    }

}
