package api.cloud.productCatalog;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ProductCatalogSteps.*;

@Epic("Product Catalog")
@Feature("Product Catalog API")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class ProductCatalogApiTest extends Tests {

    @DisplayName("Получение версии сервиса продуктовый каталог")
    @TmsLink("1080851")
    @Test
    public void getProductCatalogVersionTest() {
        Response resp = getProductCatalogVersion();
        assertNotNull(resp.jsonPath().get("build"));
        assertNotNull(resp.jsonPath().get("date"));
        assertNotNull(resp.jsonPath().get("git_hash"));
        assertNotNull(resp.jsonPath().get("stage"));
    }

    @DisplayName("Получение статуса health")
    @TmsLink("1139584")
    @Test
    public void healthTest() {
        assertEquals("ok", getHealthStatusProductCatalog());
    }
}
