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
        List<String> urls = Arrays.asList(stateServiceURL, referencesURL, sccmManager, issueCollectorService, tagsService
                , selectorAllocator);
        for (String url : urls) {
            String status = new Http(url)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/api/v1/health/")
                    .assertStatus(200)
                    .jsonPath()
                    .getString("status");
            assertEquals("ok", status);
        }
        List<String> urls2 = Arrays.asList(productCatalogURL, calculatorURL, restrictionServiceUrl, syncService, budget, day2ServiceURL,
                accountManagerURL, feedServiceURL, selectorCp, auditor, serviceManagerProxy, waitingService, secretService);
        for (String url : urls2) {
            new Http(url)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/api/v1/health")
                    .assertStatus(200);
        }
        List<String> urls3 = Arrays.asList(tarifficatorURL, iamURL, portalBackURL, orderServiceURL);
        for (String url : urls3) {
            new Http(url)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/v1/health")
                    .assertStatus(200);
        }
    }
}
