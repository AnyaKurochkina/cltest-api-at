package tests.productCatalog.productOrgInfoSystem;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.product.Product;
import models.productCatalog.ProductOrgInfoSystem;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Collections;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("ProductOrgInfoSystem")
@DisabledIfEnv("prod")
public class ProductOrgInfoSystemNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/product_org_info_system/",
            "productCatalog/productOrgInfoSystem/createInfoSystem.json");

    @DisplayName("Негативный тест на создание product_org_info_system c несуществующей организацией")
    @TmsLink("822013")
    @Test
    public void createOgrInfoSystemWithNotExistOrg() {
        Product product = Product.builder()
                .name("product_for_create_info_system_with_not_exist_org_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .build()
                .createObject();
        ProductOrgInfoSystem.builder()
                .organization("organization")
                .product(product.getProductId())
                .build().negativeCreateRequest(500);
    }

    @DisplayName("Негативный тест на создание product_org_info_system c несуществующей информационной системой")
    @TmsLink("822018")
    @Test
    public void createProductOgrInfoSystemWithNotExistInformSystem() {
        Product product = Product.builder()
                .name("product_for_create_info_system_with_not_exist_inform_system_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .build()
                .createObject();
        ProductOrgInfoSystem.builder()
                .organization("vtb")
                .informationSystems(Collections.singletonList("54564654sdf"))
                .product(product.getProductId())
                .build().negativeCreateRequest(400);
    }
}
