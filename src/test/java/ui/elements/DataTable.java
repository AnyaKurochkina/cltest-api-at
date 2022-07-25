package ui.elements;

import com.codeborne.selenide.SelenideElement;

public class DataTable extends Table{
    SelenideElement recordCount;
    SelenideElement selectPage;
    SelenideElement nextPage;
    SelenideElement previousPage;

    public DataTable(String columnName) {
        super(columnName);
        SelenideElement parent = table.parent();
        recordCount = parent.$x("button[contains(@aria-label,'Записей')]");
        selectPage = parent.$x("button[contains(@aria-label,'Страница')]");
        nextPage = parent.$x("button[contains(@aria-label,'Следующая')]");
        previousPage = parent.$x("button[contains(@aria-label,'Предыдущая')]");
    }


}
