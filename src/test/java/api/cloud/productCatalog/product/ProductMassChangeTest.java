package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.product.Product;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static steps.productCatalog.ProductSteps.createProduct;
import static steps.productCatalog.ProductSteps.massChangeProductParam;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductMassChangeTest extends Tests {

    @DisplayName("")
    @TmsLink("")
    @Test
    public void massChangeProductTest() {
        Product p1 = createProduct("product1_mass_change_test_api");
        Product p2 = createProduct("product2_mass_change_test_api");
        Product p3 = createProduct("product3_mass_change_test_api");
        Product p4 = createProduct("product4_mass_change_test_api");
        Product p5 = createProduct("product5_mass_change_test_api");
        massChangeProductParam(Arrays.asList(p1.getProductId(), p2.getProductId(), p3.getProductId(), p4.getProductId(),
                        p5.getProductId()),true);

    }
}
