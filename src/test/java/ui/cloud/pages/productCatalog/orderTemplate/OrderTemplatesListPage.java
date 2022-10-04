package ui.cloud.pages.productCatalog.orderTemplate;

import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.BaseList;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;
import ui.elements.Table;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTemplatesListPage {

    private static final String columnName = "Код шаблона";

    @Step("Проверка заголовков списка графов")
    public OrderTemplatesListPage checkHeaders() {
        Table templatesList = new Table(columnName);
        assertEquals(0, templatesList.getHeaderIndex("Наименование"));
        assertEquals(1, templatesList.getHeaderIndex(columnName));
        assertEquals(2, templatesList.getHeaderIndex("Дата создания"));
        assertEquals(3, templatesList.getHeaderIndex("Описание"));
        assertEquals(4, templatesList.getHeaderIndex("Тип"));
        assertEquals(5, templatesList.getHeaderIndex("Провайдер"));
        assertEquals(6, templatesList.getHeaderIndex("Состояние"));
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public OrderTemplatesListPage checkSortingByTitle() {
        BaseList.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду шаблона")
    public OrderTemplatesListPage checkSortingByName() {
        BaseList.checkSortingByStringField(columnName);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public OrderTemplatesListPage checkSortingByCreateDate() {
        BaseList.checkSortingByDateField("Дата создания");
        return this;
    }
}
