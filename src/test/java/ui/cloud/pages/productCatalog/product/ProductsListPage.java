package ui.cloud.pages.productCatalog.product;

import io.qameta.allure.Step;
import models.cloud.productCatalog.product.Product;
import ui.cloud.pages.productCatalog.BaseListPage;
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

    @Step("Проверка обязательных параметров при создании продукта")
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
}
