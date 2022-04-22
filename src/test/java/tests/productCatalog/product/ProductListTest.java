package tests.productCatalog.product;

import core.helper.Configure;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.product.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.product.getProducts.response.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Product;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/products/",
            "productCatalog/products/createProduct.json");

    @DisplayName("Получение списка продуктов")
    @TmsLink("643387")
    @Test
    public void getProductList() {
        String productName = "create_product_example_for_get_list_test_api";
        Product.builder()
                .name(productName)
                .build()
                .createObject();
        List<ItemImpl> list = steps.getProductObjectList(GetProductsResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка продуктов")
    @TmsLink("679088")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetProductsResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка продуктов по фильтру is_open")
    @TmsLink("806303")
    @Test
    public void getProductListByIsOpen() {
        Product.builder()
                .name("get_products_by_status_test_api")
                .title("AtTestApiProduct")
                .build()
                .createObject();
        List<ItemImpl> productList = steps.getProductObjectList(GetProductsResponse.class, "?is_open=true");
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getIsOpen());
        }
    }

    @DisplayName("Получение списка продуктов по фильтру category")
    @TmsLink("806304")
    @Test
    public void getProductListByCategory() {
        Product.builder()
                .name("get_products_by_category_test_api")
                .title("AtTestApiProduct")
                .category("vm")
                .build()
                .createObject();
        List<ItemImpl> productList = steps.getProductObjectList(GetProductsResponse.class, "?category=vm");
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertEquals("vm", listItem.getCategory());
        }
    }
}
