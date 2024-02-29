package ui.elements;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.exception.NotFoundElementException;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.format;

@Getter
public class Table implements TypifiedElement {
    @Language("XPath")
    private static final String tableXpath = "//table[thead/tr/th[.='{}'] | thead/tr/td[.='{}']]";
    protected List<String> headers;
    protected ElementsCollection rows;
    protected ElementsCollection headersCollection;
    @Language("XPath")
    private final String xpath;

    protected void open() {
    }

    public Table(String columnName) {
        open();
        xpath = format(tableXpath, columnName, columnName);
        init($x(xpath).shouldBe(Condition.visible.because(format("Не найдена таблица по колонке '{}'", columnName))));
    }

    public Table(String columnName, int index) {
        open();
        xpath = format("(" + tableXpath + ")" + TypifiedElement.postfix, columnName, columnName, TypifiedElement.getIndex(index));
        init($x(xpath).shouldBe(Condition.visible.because(format("Не найдена таблица по колонке '{}' и индексу {}", columnName, index))));
    }

    public Table(SelenideElement table) {
        open();
        xpath = table.getSearchCriteria().replaceAll("By.xpath: ", "");
        init(table);
    }

    public Table update() {
        init($x(xpath));
        return this;
    }

    public void init(SelenideElement table) {
        $x("//div[contains(@style,'background-color: rgba(') and contains(@style,', 0.7)')]").shouldNot(Condition.exist);
        table.$x("descendant::*[text()='Идет обработка данных']").shouldNot(Condition.exist);
        headersCollection = table.$$x("thead/tr/th | thead/tr/td");
        rows = table.$$x("tbody/tr[td]").filter(Condition.not(Condition.text("Нет данных для отображения")));
        headersCollection.shouldBe(CollectionCondition.allMatch("Table is loaded", WebElement::isDisplayed));
        headers = headersCollection.texts();
    }

    @Deprecated
    //Use getRowByColumnValue(..).get()
    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public SelenideElement getRowElementByColumnValue(String column, String value) {
        for (SelenideElement e : rows) {
            if (e.$$x("td").get(getHeaderIndex(column)).hover().getText().equals(value))
                return e;
        }
        throw new NotFoundElementException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    public static Table getTableByColumnNameContains(String columnName) {
        return new Table($x("//table[thead/tr/th[contains(., '" + columnName + "')]]"));
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public Row getRowByColumnValue(String column, String value) {
        Row row;
        try {
            row = getRowByColumnIndex(getHeaderIndex(column), value);
        } catch (NotFoundElementException e) {
            throw new NotFoundElementException("Не найдена строка по колонке " + column + " и значению " + value);
        }
        return row;
    }

    public Row getRowByColumnIndex(int index, String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).$$x("td").get(index).hover().getText().equals(value))
                return new Row(i);
        }
        throw new NotFoundElementException();
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public Row getRowByColumnInputValue(String column, String value) {
        Row row;
        try {
            row = getRowByColumnIndexInputValue(getHeaderIndex(column), value);
        } catch (NotFoundElementException e) {
            throw new NotFoundElementException("Не найдена строка по колонке " + column + " и значению " + value);
        }
        return row;
    }

    public Row getRowByColumnIndexInputValue(int index, String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (Objects.equals(rows.get(i).$$x("td").get(index).hover().getValue(), value))
                return new Row(i);
        }
        throw new NotFoundElementException();
    }

    @Step("Получение строки по колонке '{column}' и значению в колонке '{value}'")
    public Row getRowByColumnValueContains(String column, String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).$$x("td").get(getHeaderIndex(column)).hover().getText().contains(value))
                return new Row(i);
        }
        throw new NotFoundElementException("Не найдена строка по колонке " + column + " и значению " + value);
    }

    @Step("Проверка, что в колонке '{column}' есть значение, равное '{value}'")
    public boolean isColumnValueEquals(String column, String value) {
        if (isEmpty())
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
        if (isEmpty())
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
     *
     * @return int
     */
    public int getHeaderIndex(String column) {
        int index = headers.indexOf(column);
        Assertions.assertNotEquals(-1, index, String.format("Колонка %s не найдена. Колонки: %s", column, StringUtils.join(headers, ",")));
        return index;
    }

    @AllArgsConstructor
    @Getter
    public class Row {
        int index;

        public SelenideElement get() {
            return getRowByIndex(index);
        }

        public Table getTable() {
            return Table.this;
        }

        public String getValueByColumn(String column) {
            return getValueByColumnInRow(index, column).hover().getText();
        }

        public SelenideElement getElementByColumn(String column) {
            return getValueByColumnInRow(index, column);
        }

        public SelenideElement getElementLastColumn() {
            return getElementByColumnIndex(headers.size() - 1);
        }

        public SelenideElement getElementByColumnIndex(int column) {
            SelenideElement element;
            try {
                element = get().$$x("td").get(column);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NotFoundElementException("Нет колонки с индексом " + column);
            }
            return element;
        }

        public Asserts asserts() {
            return new Asserts();
        }

        public class Asserts {
            @Step("[Проверка] Последнее значение в строке содержит: {0}")
            public void checkLastValueOfRowContains(String value) {
                String lastValue = getElementLastColumn().getText();
                String errorMessage = String.format("Последнее значение в строке: %s, должно содержать: %s", lastValue, value);
                Assertions.assertTrue(lastValue.contains(value), errorMessage);
            }

            @Step("[Проверка] Колонка с именем '{0}' содержит значение '{1}'")
            public void checkColumnValueContains(String columnName, String value) {
                String valueByColumnName = getValueByColumn(columnName);
                String errorMessage = String.format("В колонке с именем: %s, значение: %s, должно содержать: %s", columnName, valueByColumnName, value);
                Assertions.assertTrue(valueByColumnName.contains(value), errorMessage);
            }

            @Step("[Проверка] Колонка с именем '{0}' содержит значение, равное '{1}'")
            public void checkColumnValueEquals(String columnName, String value) {
                String valueByColumnName = getValueByColumn(columnName);
                String errorMessage = String.format("В колонке с именем: %s, значение: %s, должно равняться: %s", columnName, valueByColumnName, value);
                Assertions.assertTrue(valueByColumnName.equals(value), errorMessage);
            }
        }
    }

    public String getFirstValueByColumn(String column) {
        return getValueByColumnInFirstRow(column).hover().getText();
    }

    @Step("Получение значения по колонке '{column}' в строке #{rowIndex}")
    public SelenideElement getValueByColumnInRow(int rowIndex, String column) {
        SelenideElement row = getRow(rowIndex).get();
        SelenideElement element;
        try {
            element = row.$$x("td").get(getHeaderIndex(column));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NotFoundElementException(String.format("Нет колонки с индексом %d. Всего колонок %d", getHeaderIndex(column), row.$$x("td").size()), e);
        }
        return element.shouldBe(Condition.visible);
    }

    public SelenideElement getValueByColumnInFirstRow(String column) {
        return getValueByColumnInRow(0, column);
    }

    public List<String> getNotEmptyHeaders() {
        List<String> list = new ArrayList<>(headers);
        list.removeAll(Collections.singletonList(""));
        list.removeAll(Collections.singletonList(" "));
        return list;
    }

    public static boolean isExist(String column) {
        return Waiting.sleep(() -> $x(tableXpath, column).isDisplayed(), Duration.ofSeconds(10));
    }

    public Asserts asserts() {
        return new Asserts();
    }

    public class Asserts {

        @Step("[Проверка] Колонка: {0} содержит значение: {1}")
        public void checkColumnContainsValue(String column, String value) {
            String errorMessage;
            errorMessage = column.isEmpty()
                    ? String.format("Колонка должна содержать значение: %s", value)
                    : String.format("Колонка с именем: %s должна содержать значение: %s", column, value);
            Assertions.assertTrue(isColumnValueContains(column, value), errorMessage);
        }

        @Step("[Проверка] Колонка: {0} не содержит значение: {1}")
        public void checkColumnNotContainsValue(String column, String value) {
            String errorMessage;
            errorMessage = column.isEmpty()
                    ? String.format("Колонка не должна содержать значение: %s", value)
                    : String.format("Колонка с именем: %s не должна содержать значение: %s", column, value);
            Assertions.assertFalse(isColumnValueContains(column, value), errorMessage);
        }

        @Step("[Проверка] Колонка '{0}' содержит значение, равное '{1}'")
        public void checkColumnValueEquals(String column, String value) {
            String errorMessage = column.isEmpty()
                    ? String.format("Колонка должна содержать значение '%s'", value)
                    : String.format("Колонка с именем '%s' должна содержать значение, равное '%s'", column, value);
            Assertions.assertTrue(isColumnValueEquals(column, value), errorMessage);
        }

        @Step("[Проверка] Колонка '{0}' не содержит значение, равное '{1}'")
        public void checkColumnValueNotEquals(String column, String value) {
            String errorMessage = column.isEmpty()
                    ? String.format("Колонка не должна содержать значение, равное '%s'", value)
                    : String.format("Колонка с именем '%s' не должна содержать значение, равное '%s'", column, value);
            Assertions.assertFalse(isColumnValueEquals(column, value), errorMessage);
        }
    }
}
