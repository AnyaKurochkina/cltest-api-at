package tests.productCatalog.productOrgInfoSystem;

import core.helper.Configure;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.productOrgInfoSystem.createInfoSystem.CreateInfoSystemResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Product;
import models.productCatalog.ProductOrgInfoSystem;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("ProductOrgInfoSystem")
@DisabledIfEnv("prod")
public class ProductOrgInfoSystemTest extends Tests {

    String orgName = "vtb";

    ProductCatalogSteps steps = new ProductCatalogSteps("product_org_info_system/",
            "productCatalog/productOrgInfoSystem/createInfoSystem.json", Configure.ProductCatalogURL);

    @Test
    @DisplayName("Создание productOrgInfoSystem")
    @TmsLink("")
    public void createInfoSystem() {
        Product product = Product.builder()
                .name("product_for_create_info_system_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .build()
                .createObject();
        ProductOrgInfoSystem productOrgInfoSystem = ProductOrgInfoSystem.builder()
                .organization(orgName)
                .product(product.getProductId())
                .build().createObject();
        CreateInfoSystemResponse createdInfoSystem = steps.getProductOrgInfoSystem(product.getProductId(), orgName);
        assertEquals(createdInfoSystem.getId(), productOrgInfoSystem.getId());
    }

    @Test
    @DisplayName("Удаление productOrgInfoSystem")
    @TmsLink("")
    public void deleteInfoSystem() {
        Product product = Product.builder()
                .name("product_for_delete_info_system_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .build()
                .createObject();
        ProductOrgInfoSystem productOrgInfoSystem = ProductOrgInfoSystem.builder()
                .organization(orgName)
                .product(product.getProductId())
                .build().createObject();
        GetImpl getProduct = steps.getById(productOrgInfoSystem.getId(), CreateInfoSystemResponse.class);
        assertEquals(getProduct.getId(), productOrgInfoSystem.getId());
    }
}
