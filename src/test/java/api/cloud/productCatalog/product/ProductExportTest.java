package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.product.Product;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductSteps;

import java.util.Arrays;

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
}
