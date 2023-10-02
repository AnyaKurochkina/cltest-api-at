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

@Tag("health_check")
@Epic("Health Check")
@Feature("Health Check")
public class HealthCheckTest extends Tests {

    @Test
    public void healthCheckTest() {
        List<String> urls = Arrays.asList(StateServiceURL, ProductCatalogURL, TarifficatorURL, CalculatorURL, IamURL, PortalBackURL, OrderServiceURL,
                Day2ServiceURL, ReferencesURL, RestrictionServiceUrl, SyncService, Budget);
        for (String url : urls) {
             new Http(url)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/api/v1/health")
                    .assertStatus(200)
                    .jsonPath()
                    .getString("status");
        }
    }
}
