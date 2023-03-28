package api.cloud.productCatalog.product;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.product.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.ProductSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductImportTest extends Tests {

    @DisplayName("Импорт продукта")
    @TmsLink("643393")
    @Test
    public void importProductTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProduct.json");
        String name = new JsonPath(data).get("Product.name");
        if (isProductExists(name)) {
            deleteProductByName(name);
        }
        ImportObject importObject = importProduct(RESOURCE_PATH + "/json/productCatalog/products/importProduct.json");
        assertEquals(name, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isProductExists(name));
        deleteProductByName(name);
        assertFalse(isProductExists(name));
    }

    @DisplayName("Импорт нескольких продуктов")
    @TmsLink("1522943")
    @Test
    public void importProductsTest() {
        String productName = "multi_import_product_test_api";
        if (isProductExists(productName)) {
            deleteProductByName(productName);
        }
        String productName2 = "multi_import_product2_test_api";
        if (isProductExists(productName2)) {
            deleteProductByName(productName2);
        }
        String graphId = createGraph(RandomStringUtils.randomAlphabetic(10).toLowerCase()).getGraphId();
        Product product = getCreateProductResponse(Product.builder()
                .name(productName)
                .graphId(graphId)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Product.class);
        Product product2 = getCreateProductResponse(Product.builder()
                .name(productName2)
                .graphId(graphId)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Product.class);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/products/multiImportProduct.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/products/multiImportProduct2.json";
        DataFileHelper.write(filePath, exportProductById(product.getProductId()).toString());
        DataFileHelper.write(filePath2, exportProductById(product2.getProductId()).toString());
        deleteProductByName(productName);
        deleteProductByName(productName2);
        importObjects("products", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isProductExists(productName), "Продукт не существует");
        assertTrue(isProductExists(productName2), "Продукт не существует");
        deleteProductByName(productName);
        deleteProductByName(productName2);
    }

    @DisplayName("Импорт продукта c иконкой")
    @TmsLink("1085928")
    @Test
    public void importProductWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProductWithIcon.json");
        String name = new JsonPath(data).get("Product.name");
        if (isProductExists(name)) {
            deleteProductByName(name);
        }
        importProduct(RESOURCE_PATH + "/json/productCatalog/products/importProductWithIcon.json");
        Product product = getProductById(getProductByName(name).getProductId());
        assertFalse(product.getIconStoreId().isEmpty());
        assertFalse(product.getIconUrl().isEmpty());
        assertTrue(isProductExists(name), "Продукт не существует");
        deleteProductByName(name);
        assertFalse(isProductExists(name), "Продукт существует");
    }

    @DisplayName("Импорт уже существующего продукта")
    @TmsLink("1535300")
    @Test
    public void importExistProductTest() {
        Product product = createProductByName("import_exist_product_test_api");
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/products/existProductImport.json";
        DataFileHelper.write(filePath, exportProductById(product.getProductId()).toString());
        ImportObject importObject = importProduct(filePath);
        DataFileHelper.delete(filePath);
        assertEquals("error", importObject.getStatus());
        assertEquals( String.format("Error loading dump: Версия \"%s\" Product:%s уже существует. Измените значение версии (\"version_arr: [1, 0, 0]\") у импортируемого объекта и попробуйте снова.",
                        product.getVersion(), product.getName()),
                importObject.getMessages().get(0));
    }
}
