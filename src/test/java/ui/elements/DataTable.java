package ui.elements;

import com.codeborne.selenide.SelenideElement;

import java.util.ArrayList;
import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class DataTable extends Table {
    SelenideElement recordCount;
    SelenideElement selectPage;
    SelenideElement nextPage;
    SelenideElement previousPage;

    SelenideElement btnAdd = $x("//*[contains(@data-testid,'add-button')]//button");

    public DataTable(String columnName) {
        super(columnName);
        SelenideElement parent = table.parent();
        recordCount = parent.$x("button[contains(@aria-label,'Записей')]");
        selectPage = parent.$x("button[contains(@aria-label,'Страница')]");
        nextPage = parent.$x("button[contains(@aria-label,'Следующая')]");
        previousPage = parent.$x("button[contains(@aria-label,'Предыдущая')]");
    }

    public void clickAdd() {
        Button.byElement(btnAdd).click();
    }

    public List<String> getColumnValuesList(String column) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < rowSize(); i++)
            values.add(getValueByColumnInRow(i, column).getText());
        return values;
    }
}
