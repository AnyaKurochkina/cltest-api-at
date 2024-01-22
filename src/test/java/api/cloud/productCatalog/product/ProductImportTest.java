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
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductSteps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static core.utils.AssertUtils.assertEqualsList;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductCatalogSteps.*;
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
        Product product = ProductSteps.createProduct("import_exist_product_test_api");
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/products/existProductImport.json";
        DataFileHelper.write(filePath, exportProductById(product.getProductId()).toString());
        ImportObject importObject = importProduct(filePath);
        DataFileHelper.delete(filePath);
        assertEquals("error", importObject.getStatus());
        assertEquals(String.format("Error loading dump: Версия \"%s\" Product:%s уже существует, но с другим наполнением. Измените значение версии (\"version_arr: [1, 0, 0]\") у импортируемого объекта и попробуйте снова.",
                        product.getVersion(), product.getName()),
                importObject.getMessages().get(0));
    }

    @DisplayName("Импорт продукта с tag_list")
    @TmsLink("SOUL-7114")
    @Test
    public void importProductWithTagListTest() {
        String productName = "product_import_with_tag_list_test_api";
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/products/importProductWithTags.json";
        if (isProductExists(productName)) {
            deleteProductByName(productName);
        }
        Graph graph = createGraph("graph_product_import_for_export_with_tags_test");
        List<String> expectedTagList = Arrays.asList("import_test", "test_import");
        JSONObject jsonObject = Product.builder()
                .name(productName)
                .graphId(graph.getGraphId())
                .tagList(expectedTagList)
                .build()
                .toJson();
        Product product = createProduct(jsonObject);
        DataFileHelper.write(filePath, exportObjectByIdWithTags("products", product.getProductId()).toString());
        deleteProductByName(productName);
        importObjectWithTagList("products", filePath);
        assertEquals(expectedTagList, getProductByName(productName).getTagList());
        deleteProductByName(productName);
    }

    @DisplayName("Добавление новых tags при импорте продукта")
    @TmsLink("SOUL-7122")
    @Test
    public void checkNewTagsAddedWhenImportProductTest() {
        String productName = "product_import_with_new_tag_list_test_api";
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/products/importProductWithNewTags.json";
        if (isProductExists(productName)) {
            deleteProductByName(productName);
        }
        Graph graph = createGraph("graph_product_import_for_export_with_new_tags_test");
        List<String> addTagList = Collections.singletonList("new_tag");
        JSONObject jsonObject = Product.builder()
                .name(productName)
                .graphId(graph.getGraphId())
                .tagList(Arrays.asList("import_test", "test_import"))
                .build()
                .toJson();
        Product product = createProduct(jsonObject);
        DataFileHelper.write(filePath, exportObjectByIdWithTags("products", product.getProductId()).toString());
        String updatedJsonForImport = JsonHelper.getJsonTemplate("/productCatalog/products/importProductWithNewTags.json")
                .set("Product.tag_name_list", addTagList)
                .set("Product.version_arr", Arrays.asList(1, 0, 1))
                .build()
                .toString();
        DataFileHelper.write(filePath, updatedJsonForImport);
        importObjectWithTagList("products", filePath);
        List<String> expectedTags = Arrays.asList("new_tag", "import_test", "test_import");
        List<String> actualTags = getProductByName(productName).getTagList();
        assertEqualsList(expectedTags, actualTags);
        deleteProductByName(productName);
    }

    @DisplayName("Проверка не обновления неверсионных полей при импорте уже существующего продукта")
    @TmsLink("SOUL-7457")
    @Test
    public void checkNotVersionedFieldsWhenImportedExistProductTest() {
        String description = "update description";
        String productName = "check_not_versioned_fields__when_import_exist_product_test_api";
        Product product = createProduct(productName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/products/checkNotVersionedFieldsExistProductImport.json";
        DataFileHelper.write(filePath, exportProductById(product.getProductId()).toString());
        partialUpdateProduct(product.getProductId(), new JSONObject().put("description", description));
        importProduct(filePath);
        DataFileHelper.delete(filePath);
        Product productById = getProductById(product.getProductId());
        assertEquals(description, productById.getDescription());
    }

    @DisplayName("Проверка current_version при импорте уже существующего продукта")
    @TmsLink("SOUL-7771")
    @Test
    public void checkCurrentVersionWhenAlreadyExistProductImportTest() {
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/products/checkCurrentVersion.json";
        Product product = Product.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase())
                .version("1.0.1")
                .build()
                .createObject();
        DataFileHelper.write(filePath, exportProductById(product.getProductId()).toString());
        product.deleteObject();
        Product createdProduct = Product.builder()
                .name(product.getName())
                .version("1.0.0")
                .build()
                .createObject();
        partialUpdateProduct(createdProduct.getProductId(), new JSONObject()
                .put("max_count", 6)
                .put("version", "1.1.1"));
        partialUpdateProduct(createdProduct.getProductId(), new JSONObject()
                .put("current_version", "1.1.1"));
        importProduct(filePath);
        DataFileHelper.delete(filePath);
        Product productById = getProductById(createdProduct.getProductId());
        assertEquals("1.1.1", productById.getCurrentVersion());
    }
}
