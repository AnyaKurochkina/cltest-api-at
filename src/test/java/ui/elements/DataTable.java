package ui.elements;

import com.codeborne.selenide.SelenideElement;
import core.exception.NotFoundElementException;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static core.helper.StringUtils.$x;

public class DataTable extends Table {
    SelenideElement recordCount;
    Select selectPage;
    SelenideElement nextPage;
    SelenideElement previousPage;

    SelenideElement btnAdd = $x("//*[contains(@data-testid,'add-button')]//button");

    public DataTable(String columnName) {
        super(columnName);
        @Language("XPath") String xpath = getXpath().substring(2);
        recordCount = TypifiedElement.getNearElement("button[contains(@aria-label,'Записей')]", xpath);
        selectPage = new Select(TypifiedElement.getNearElement("button[contains(@aria-label,'Страница')]", xpath));
        nextPage = TypifiedElement.getNearElement("button[contains(@aria-label,'Следующая')]", xpath);
        previousPage = TypifiedElement.getNearElement("button[contains(@aria-label,'Предыдущая')]", xpath);
    }

    public boolean isNextPage() {
        return nextPage.isEnabled();
    }

    public boolean isPreviousPage() {
        return previousPage.isEnabled();
    }

    public void selectPage(int page) {
        selectPage.set(String.valueOf(page));
    }

    public void nextPage() {
        selectPage(Integer.parseInt(selectPage.getValue()) + 1);
        update();
    }

    public void clickAdd() {
        Button.byElement(btnAdd).click();
    }

    public List<String> getColumnValuesListPage(String column) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < rowSize(); i++)
            values.add(getValueByColumnInRow(i, column).getText());
        return values;
    }

    public List<String> getColumnValuesList(String column) {
        List<String> values = getColumnValuesListPage(column);
        while (isNextPage()) {
            nextPage();
            values.addAll(getColumnValuesListPage(column));
        }
        selectPage(1);
        return values;
    }

    @SneakyThrows
    @Step("Поиск страницы с подходящим условием")
    public DataTable searchAllPages(Predicate<DataTable> condition) {
        if (isPreviousPage())
            selectPage(1);
        while (true) {
            if (condition.test(this))
                return this;
            if (!isNextPage())
                break;
            nextPage();
        }
        throw new NotFoundElementException("Не найдена страница с подходящим условием");
    }

    public DataTable update() {
        init($x(getXpath()));
        return this;
    }
}
