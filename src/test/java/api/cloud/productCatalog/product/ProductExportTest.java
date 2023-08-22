package api.cloud.productCatalog.product;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        simpleProduct = createProductByName("export_product1_test_api");
        simpleProduct2 = createProductByName("export_product2_test_api");
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
        Product product = createProductByName("product_export_test_api");
        exportProductById(product.getProductId());
    }

    @DisplayName("Экспорт продукта по имени")
    @TmsLink("1361371")
    @Test
    public void exportProductByNameTest() {
        Product product = createProductByName("product_export_by_name_test_api");
        exportProductByName(product.getName());
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте продукта")
    @TmsLink("SOUL-")
    @Test
    public void checkExportedObjectsFieldProductTest() {
        String productName = "product_exported_objects_test_api";
        Product product = createProductByName(productName);
        Response response = exportProductById(product.getProductId());
        LinkedHashMap r = response.jsonPath().get("exported_objects.Product.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(product.getLastVersion(), jsonObject.get("last_version_str").toString());
        assertEquals(product.getName(), jsonObject.get("name").toString());
        assertEquals(product.getVersion(), jsonObject.get("version").toString());
    }
}
