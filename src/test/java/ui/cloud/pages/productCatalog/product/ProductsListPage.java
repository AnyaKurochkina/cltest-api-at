package ui.cloud.pages.productCatalog.product;

import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.InputFile;
import ui.elements.Select;
import ui.elements.Table;

public class ProductsListPage extends BaseListPage {

    private static final String nameColumn = "Код продукта";
    private final Select categorySelect = Select.byLabel("Категория");
    private final Select statusSelect = Select.byLabel("Статус");
    private final SelenideElement importButton = searchInput.getInput().$x("./following::button[2]");

    @Step("Проверка заголовков списка продуктов")
    public ProductsListPage checkHeaders() {
        AssertUtils.assertHeaders(new Table(nameColumn),
                "Наименование", nameColumn, "Дата создания", "Категория", "Статус", "", "");
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
        TestUtils.wait(1000);
        return new ProductPage();
    }

    @Step("Открытие страницы продукта '{name}'")
    public ProductPage openProductPage(String name) {
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

    @Step("Проверка, что продукт '{product.name}' найден при поиске по значению '{value}'")
    public ProductsListPage findProductByValue(String value, Product product) {
        search(value);
        Assertions.assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, product.getName()));
        return this;
    }

    @Step("Копирование продукта '{product.name}'")
    public ProductsListPage copy(Product product) {
        new BaseListPage().copy(nameColumn, product.getName());
        Alert.green("Копирование выполнено успешно");
        return this;
    }

    @Step("Проверка сортировки списка продуктов")
    public ProductsListPage checkSorting() {
        checkSortingByStringField("Наименование");
        checkSortingByStringField(nameColumn);
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
        Assertions.assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, product.getName()));
        return this;
    }

    @Step("Проверка, что продукт '{product.name}' не отображается в списке")
    public ProductsListPage checkProductIsNotDisplayed(Product product) {
        Assertions.assertFalse(new Table(nameColumn).isColumnValueEquals(nameColumn, product.getName()));
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
        clearFiltersButton.click();
        TestUtils.wait(500);
        return this;
    }

    @Step("Импорт продукта из файла '{path}'")
    public ProductsListPage importProduct(String path) {
        importButton.click();
        new InputFile(path).importFileAndSubmit();
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
        checkRowIsHighlighted(nameColumn, name);
    }
}
