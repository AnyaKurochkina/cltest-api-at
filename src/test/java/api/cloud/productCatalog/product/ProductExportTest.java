package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductSteps;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductCatalogSteps.exportObjectByIdWithTags;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;
import static steps.productCatalog.ProductSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductExportTest extends Tests {
    private static Product simpleProduct;
    private static Product simpleProduct2;

    @BeforeAll
    public static void setUp() {
        simpleProduct = ProductSteps.createProduct("export_product1_test_api");
        simpleProduct2 = ProductSteps.createProduct("export_product2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких продуктов")
    @TmsLink("1522970")
    @Test
    public void exportProductsTest() {
        ExportEntity e = new ExportEntity(simpleProduct.getProductId(), simpleProduct.getVersion());
        ExportEntity e2 = new ExportEntity(simpleProduct2.getProductId(), simpleProduct2.getVersion());
        exportObjectsById("products", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт продукта по Id")
    @TmsLink("642499")
    @Test
    public void exportProductByIdTest() {
        Product product = ProductSteps.createProduct("product_export_test_api");
        exportProductById(product.getProductId());
    }

    @DisplayName("Экспорт продукта по имени")
    @TmsLink("1361371")
    @Test
    public void exportProductByNameTest() {
        Product product = ProductSteps.createProduct("product_export_by_name_test_api");
        exportProductByName(product.getName());
    }

    @DisplayName("Экспорт продукта по Id с tag_list")
    @TmsLink("SOUL-7113")
    @Test
    public void exportProductByIdWithTagListTest() {
        String productName = "product_export_with_tag_list_test_api";
        if (isProductExists(productName)) {
            deleteProductByName(productName);
        }
        Graph graph = createGraph("graph_for_product_export_with_tags_test");
        List<String> expectedTagList = Arrays.asList("export_test", "test2");
        JSONObject jsonObject = Product.builder()
                .name(productName)
                .graphId(graph.getGraphId())
                .tagList(expectedTagList)
                .build()
                .toJson();
        Product product = createProduct(jsonObject);
        List<String> actualTagList = exportObjectByIdWithTags("products", product.getProductId()).jsonPath().getList("Product.tag_name_list");
        assertEquals(actualTagList, expectedTagList);
        deleteProductById(product.getProductId());
    }
}
