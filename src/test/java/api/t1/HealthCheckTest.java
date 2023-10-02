package api.t1;

import api.Tests;
import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static core.helper.Configure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("health_check")
@Epic("Health Check")
@Feature("Health Check")
public class HealthCheckTest extends Tests {

    @Test
    public void healthCheckTest() {
        List<String> urls = Arrays.asList(StateServiceURL, ReferencesURL);
        for (String url : urls) {
            String status = new Http(url)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/api/v1/health/")
                    .assertStatus(200)
                    .jsonPath()
                    .getString("status");
            assertEquals("ok", status);
        }
        List<String> urls2 = Arrays.asList(ProductCatalogURL, CalculatorURL, RestrictionServiceUrl, SyncService, Budget, Day2ServiceURL);
        for (String url : urls2) {
            new Http(url)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/api/v1/health")
                    .assertStatus(200);
        }
        List<String> urls3 = Arrays.asList(TarifficatorURL, IamURL, PortalBackURL, OrderServiceURL);
        for (String url : urls3) {
            new Http(url)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/v1/health")
                    .assertStatus(200);
        }
    }
}
