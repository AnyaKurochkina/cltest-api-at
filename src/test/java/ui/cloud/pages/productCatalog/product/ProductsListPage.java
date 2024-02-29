package ui.cloud.pages.productCatalog.product;

import com.codeborne.selenide.Condition;
import core.helper.StringUtils;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.FileImportDialog;
import ui.elements.Select;
import ui.elements.Table;

public class ProductsListPage extends EntityListPage {

    public static final String PRODUCT_NAME_COLUMN = "Код продукта";
    private final Select categorySelect = Select.byLabel("Категория");
    private final Select statusSelect = Select.byLabel("Статус");

    @Step("Проверка заголовков списка продуктов")
    public ProductsListPage checkHeaders() {
        AssertUtils.assertHeaders(new Table(PRODUCT_NAME_COLUMN),
                "Наименование", PRODUCT_NAME_COLUMN, "Дата создания", "Дата изменения", "Категория", "Статус", "Теги", "", "");
        return this;
    }

    @Step("Создание продукта '{product.name}'")
    public ProductPage createProduct(Product product) {
        addNewObjectButton.click();
        ProductPage productPage = new ProductPage().setAttributes(product).saveWithoutPatchVersion("Продукт успешно создан");
        Waiting.sleep(500);
        return productPage;
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

    @Step("Поиск и открытие страницы продукта '{name}'")
    public ProductPage findAndOpenProductPage(String name) {
        search(name);
        openProductPage(name);
        return new ProductPage();
    }

    @Step("Открытие страницы продукта '{name}'")
    public ProductPage openProductPage(String name) {
        new Table(PRODUCT_NAME_COLUMN).getRowByColumnValue(PRODUCT_NAME_COLUMN, name).get()
                .shouldBe(Condition.visible.because("Должна отображаться строка с продуктом")).click();
        return new ProductPage();
    }

    @Step("Удаление продукта '{name}'")
    public ProductsListPage delete(String name) {
        search(name);
        delete(PRODUCT_NAME_COLUMN, name);
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
        Assertions.assertTrue(new Table(PRODUCT_NAME_COLUMN).isEmpty());
        return this;
    }

    @Step("Проверка, что продукт '{product.name}' найден при поиске по значению '{value}'")
    public ProductsListPage findProductByValue(String value, Product product) {
        search(value);
        Assertions.assertTrue(new Table(PRODUCT_NAME_COLUMN).isColumnValueEquals(PRODUCT_NAME_COLUMN, product.getName()));
        return this;
    }

    @Step("Копирование продукта '{product.name}'")
    public ProductsListPage copy(Product product) {
        copy(PRODUCT_NAME_COLUMN, product.getName());
        Alert.green("Копирование выполнено успешно");
        return this;
    }

    @Step("Проверка сортировки списка продуктов")
    public ProductsListPage checkSorting() {
        checkSortingByStringField("Наименование");
        checkSortingByStringField(PRODUCT_NAME_COLUMN);
        checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Выбор в фильтре по категории значения '{value}'")
    public ProductsListPage setCategoryFilter(String value) {
        categorySelect.set(value);
        return this;
    }

    @Step("Выбор в фильтре по статусу значения '{value}'")
    public ProductsListPage setStatusFilter(String value) {
        statusSelect.set(value);
        return this;
    }

    @Step("Применение фильтров")
    public ProductsListPage applyFilters() {
        applyFiltersButton.click();
        Waiting.sleep(500);
        return this;
    }

    @Step("Проверка, что продукт '{product.name}' отображается в списке")
    public ProductsListPage checkProductIsDisplayed(Product product) {
        Assertions.assertTrue(new Table(PRODUCT_NAME_COLUMN).isColumnValueEquals(PRODUCT_NAME_COLUMN, product.getName()));
        return this;
    }

    @Step("Проверка, что продукт '{product.name}' не отображается в списке")
    public ProductsListPage checkProductIsNotDisplayed(Product product) {
        Assertions.assertFalse(new Table(PRODUCT_NAME_COLUMN).isColumnValueEquals(PRODUCT_NAME_COLUMN, product.getName()));
        return this;
    }

    @Step("Удаление заданного значения фильтра '{value}'")
    public ProductsListPage removeFilterTag(String value) {
        TestUtils.scrollToTheTop();
        StringUtils.$x("//span[text()='{}']/following-sibling::*[name()='svg']", value).click();
        TestUtils.wait(500);
        return this;
    }

    @Step("Сброс фильтров")
    public ProductsListPage clearFilters() {
        clearFiltersButton.getButton().scrollIntoView(false).click();
        Waiting.sleep(500);
        return this;
    }

    @Step("Импорт продукта из файла '{path}'")
    public ProductsListPage importProduct(String path) {
        importButton.click();
        new FileImportDialog(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }

    @Step("Сортировка по дате создания")
    public ProductsListPage sortByCreateDate() {
        sortByCreateDate.click();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public ProductsListPage lastPage() {
        lastPageV2();
        return this;
    }

    @Step("Проверка, что подсвечен продукт '{name}'")
    public void checkProductIsHighlighted(String name) {
        checkRowIsHighlighted(PRODUCT_NAME_COLUMN, name);
    }
}
