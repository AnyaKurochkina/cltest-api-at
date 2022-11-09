package tests.productCatalog.product;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.product.Product;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ProductSteps.getProductById;
import static steps.productCatalog.ProductSteps.getProductViewerById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductRestrictedAndAllowedGroupsTest extends Tests {

    @DisplayName("Создание продукта с ограничением restricted group на уровне realm")
    @TmsLink("1279114")
    @Test
    public void productRestrictedGroupRealmLevelTest() {
        Product product = Product.builder()
                .name("product_for_restricted_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .build()
                .createObject();
        Product productById = getProductById(product.getProductId());
        assertNotNull(productById);
        String msg = getProductViewerById(product.getProductId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание продукта с ограничением allowed_group на уровне realm")
    @TmsLink("1282722")
    @Test
    public void productAllowedGroupRealmLevelTest() {
        Product product = Product.builder()
                .name("product_for_allowed_group_realm_lvl_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList("superadmin"))
                .build()
                .createObject();
        Product productById = getProductById(product.getProductId());
        assertNotNull(productById);
        String msg = getProductViewerById(product.getProductId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание продукта с ограничением restricted_group на уровне realm и ограничением allowed_group на уровне account")
    @TmsLink("1282744")
    @Test
    public void productRestrictedGroupRealmLevelAndAllowedGroupAccountTest() {
        Product product = Product.builder()
                .name("product_for_restricted_group_and_allowed_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .allowedGroups(Collections.singletonList("account:test"))
                .build()
                .createObject();
        Product productById = getProductById(product.getProductId());
        assertNotNull(productById);
        String msg = getProductViewerById(product.getProductId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание продукта с ограничением allowed_group на уровне realm и ограничением restricted_group на уровне account")
    @TmsLink("1282745")
    @Test
    public void productAllowedGroupRealmLevelAndRestrictedGroupAccountTest() {
        Product product = Product.builder()
                .name("product_for_allowed_group_realm_and_restricted_group_account_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("account:role2_api_tests"))
                .allowedGroups(Collections.singletonList("superadmin"))
                .build()
                .createObject();
        Product productById = getProductById(product.getProductId());
        assertNotNull(productById);
        String msg = getProductViewerById(product.getProductId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание продукта с ограничением allowed_group на уровне account")
    @TmsLink("1282755")
    @Test
    public void actionAllowedGroupAccountTest() {
        Product product = Product.builder()
                .name("product_for_allowed_group_account_lvl_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList("account:role_api_tests"))
                .build()
                .createObject();
        Product productById = getProductById(product.getProductId());
        assertNotNull(productById);
        String msg = getProductViewerById(product.getProductId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание продукта с ограничением restricted_group на уровне account")
    @TmsLink("1282775")
    @Test
    public void actionRestrictedGroupAccountTest() {
        Product product = Product.builder()
                .name("product_for_restricted_group_account_lvl_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("account:role2_api_tests"))
                .build()
                .createObject();
        Product productById = getProductById(product.getProductId());
        assertNotNull(productById);
        String msg = getProductViewerById(product.getProductId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }
}
