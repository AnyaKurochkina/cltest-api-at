package ui.cloud.pages.productCatalog.product;

import io.qameta.allure.Step;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Table;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductsListPage extends BaseListPage {

    private static final String nameColumn = "Код продукта";

    @Step("Проверка заголовков списка продуктов")
    public ProductsListPage checkHeaders() {
        Table table = new Table(nameColumn);
        assertEquals(Arrays.asList("Наименование", nameColumn, "Дата создания", "Категория", "Статус", "", ""),
                table.getHeaders());
        return this;
    }

    @Step("Создание продукта '{product.name}'")
    public ProductPage createProduct(Product product) {
        addNewObjectButton.click();
        return new ProductPage().setAttributes(product).saveWithoutPatchVersion("Продукт успешно создан");
    }

    @Step("Проверка обязательных полей при создании продукта")
    public ProductsListPage checkRequiredFields(Product product) {
        addNewObjectButton.getButton().scrollIntoView(false).click();
        return new ProductPage().checkRequiredFields(product);
    }

    @Step("Проверка валидации неуникального кода продукта '{product.name}'")
    public ProductsListPage checkNonUniqueNameValidation(Product product) {
        addNewObjectButton.getButton().scrollIntoView(false).click();
        return new ProductPage().checkNonUniqueNameValidation(product);
    }

    @Step("Проверка валидации недопустимых значений в коде продукта")
    public ProductsListPage checkNameValidation(String[] names) {
        addNewObjectButton.click();
        return new ProductPage().checkNameValidation(names);
    }

    @Step("Задание в строке поиска значения 'value'")
    private ProductsListPage search(String value) {
        searchInput.setValue(value);
        TestUtils.wait(1000);
        return this;
    }

    @Step("Поиск и открытие страницы продукта '{name}'")
    public ProductPage findAndOpenProductPage(String name) {
        search(name);
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        TestUtils.wait(500);
        return new ProductPage();
    }

    @Step("Удаление продукта '{name}'")
    public ProductsListPage delete(String name) {
        search(name);
        delete(nameColumn, name);
        new DeleteDialog().inputValidIdAndDelete("Удаление выполнено успешно");
        Assertions.assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }
}
