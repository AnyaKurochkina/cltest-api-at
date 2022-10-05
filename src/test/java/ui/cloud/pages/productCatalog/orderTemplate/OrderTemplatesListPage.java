package ui.cloud.pages.productCatalog.orderTemplate;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import models.productCatalog.ItemVisualTemplate;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseList;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Input;
import ui.elements.Table;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTemplatesListPage {

    private static final String columnName = "Код шаблона";
    private final Input searchInput = Input.byPlaceholder("Поиск");

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

    @Step("Проверка сортировки по состоянию")
    public OrderTemplatesListPage checkSortingByState() {
        String header = "Состояние";
        Table table = new Table(columnName);
        SelenideElement columnHeader = StringUtils.$x("//div[text()='{}']/parent::div", header);
        SelenideElement arrowIcon = StringUtils.$x("//div[text()='{}']/following-sibling::*[name()='svg']", header);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstElementState = table.getValueByColumnInFirstRow(header).$x(".//*[name()='svg']/parent::div")
                .getAttribute("Title");
        Assertions.assertEquals("Выключено", firstElementState);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstElementState = table.getValueByColumnInFirstRow(header).$x(".//*[name()='svg']/parent::div")
                .getAttribute("Title");
        Assertions.assertEquals("Включено", firstElementState);
        return this;
    }

    @Step("Проверка, что шаблон '{template.name}' найден при поиске по значению '{value}'")
    public OrderTemplatesListPage findTemplateByValue(String value, ItemVisualTemplate template) {
        searchInput.setValue(value);
        TestUtils.wait(1000);
        Assertions.assertTrue(new Table(columnName).isColumnValueEquals(columnName, template.getName()));
        return this;
    }
}
