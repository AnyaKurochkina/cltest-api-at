package ui.elements;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static core.helper.StringUtils.$x;

public class Table implements TypifiedElement {
    @Getter
    List<String> headers;
    SelenideElement table;
    ElementsCollection rows;
    ElementsCollection headersCollection;

    protected void open() {}

    public Table(String columnName) {
        open();
        table = $x("//table[thead/tr/th[.='{}']]", columnName).shouldBe(Condition.visible);

        $x("//div[contains(@style,'background-color: rgba(') and contains(@style,', 0.7)')]").shouldNot(Condition.exist);
        $x("//table[contains(.,'Идет обработка данных')]").shouldNot(Condition.exist);

        headersCollection = table.$$x("thead/tr/th");
        rows = table.$$x("tbody/tr");
        headersCollection.shouldBe(CollectionCondition.allMatch("Table is loaded", WebElement::isDisplayed));
        headers = headersCollection.shouldBe(CollectionCondition.allMatch("", WebElement::isDisplayed)).texts();
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public SelenideElement getRowElementByColumnValue(String column, String value) {
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getIndexHeader(column)).hover().getText().equals(value))
                return e;
        }
        throw new NotFoundException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public Row getRowByColumnValue(String column, String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).$$x("td").get(getIndexHeader(column)).hover().getText().equals(value))
                return new Row(i);
        }
        throw new NotFoundException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    @Step("Проверка существования в колонке '{column}' значения '{value}'")
    public boolean isColumnValueExist(String column, String value) {
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getIndexHeader(column)).hover().getText().equals(value))
                return true;
        }
        return false;
    }

    public SelenideElement getRowByIndex(int index) {
        Assertions.assertTrue(rows.size() > index, "Индекс больше кол-ва строк");
        return rows.get(0);
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
            return getValueByColumnInRow(row, column).hover().getText();
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
