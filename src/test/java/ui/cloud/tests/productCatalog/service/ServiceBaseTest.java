package ui.cloud.tests.productCatalog.service;

import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import io.qameta.allure.Epic;
import models.productCatalog.OrgDirection;
import models.productCatalog.Service;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.UUID;

@Epic("Сервисы")
@DisabledIfEnv("prod")
public class ServiceBaseTest extends BaseTest {
    final static String TITLE = "AT UI Service";
    final static String DESCRIPTION = "Description";
    private static final ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/services/",
            "/productCatalog/services/createServices.json");
    final String NAME = UUID.randomUUID().toString();
    OrgDirection orgDirection;
    Service service;

    @BeforeEach
    public void setUp() {
        createService(NAME);
    }

    private void createService(String name) {
        orgDirection = OrgDirection.builder()
                .orgDirectionName(UUID.randomUUID().toString())
                .title("AT UI Direction")
                .build()
                .createObject();

        service = Service.builder()
                .directionId(orgDirection.getOrgDirectionId())
                .serviceName(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .version("1.0.0")
                .graphVersion("1.0.0")
                .build()
                .createObject();
    }

    void deleteService(String name) {
        steps.deleteByName(name, GetServiceListResponse.class);
    }
}
