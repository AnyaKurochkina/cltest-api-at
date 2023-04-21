package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import java.util.UUID;

@Feature("Создание продукта")
public class CreateProductTest extends ProductBaseTest {

    @Test
    @TmsLink("507403")
    @DisplayName("Создание продукта")
    public void createProductTest() {
        checkProductNameValidation();
        checkRequiredFields();
        createProductWithNonUniqueName();
        createProduct();
    }

    @Step("Проверка обязательных полей при создании продукта")
    public void checkRequiredFields() {
        Product product = Product.builder().name(NAME + "_").title(TITLE).graphId(graph.getGraphId())
                .category(Categories.VM.getValue()).categoryV2(Categories.COMPUTE).author("AT UI").build();
        new ControlPanelIndexPage().goToProductsListPage()
                .checkRequiredFields(product);
    }

    @Step("Создание продукта с неуникальным кодом")
    public void createProductWithNonUniqueName() {
        new ControlPanelIndexPage().goToProductsListPage()
                .checkNonUniqueNameValidation(Product.builder().name(NAME).title(TITLE).build());
    }

    @Step("Создание продукта с недопустимым кодом")
    public void checkProductNameValidation() {
        new ControlPanelIndexPage().goToProductsListPage()
                .checkNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }

    @Step("Создание продукта")
    public void createProduct() {
        product.setName(UUID.randomUUID().toString());
        new ControlPanelIndexPage().goToProductsListPage()
                .createProduct(product)
                .checkAttributes(product);
        deleteProductByApi(product.getName());
    }
}
