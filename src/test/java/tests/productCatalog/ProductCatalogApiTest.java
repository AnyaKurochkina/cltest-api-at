package tests.productCatalog;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ProductCatalogSteps.getHealthCheckStatus;
import static steps.productCatalog.ProductCatalogSteps.getProductCatalogVersion;

@Epic("Product Catalog")
@Feature("Product Catalog API")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class ProductCatalogApiTest {

    @DisplayName("Получение версии сервиса продуктовый каталог")
    @TmsLink("1080851")
    @Test
    public void getProductCatalogVersionTest() {
        Response resp = getProductCatalogVersion();
        assertNotNull(resp.jsonPath().get("build"));
        assertNotNull(resp.jsonPath().get("date"));
        assertNotNull(resp.jsonPath().get("git_hash"));
    }

    @DisplayName("Получение статуса health check")
    @TmsLink("")
    @Test
    public void healthCheckTest() {
        assertEquals("ok", getHealthCheckStatus());
    }
}
