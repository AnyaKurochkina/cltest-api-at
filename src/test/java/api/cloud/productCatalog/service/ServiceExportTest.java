package api.cloud.productCatalog.service;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.service.Service;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;
import static steps.productCatalog.ServiceSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServiceExportTest extends Tests {
    private static Service simpleService;
    private static Service simpleService2;

    @BeforeAll
    public static void setUp() {
        simpleService = createService("export_service1_test_api");
        simpleService2 = createService("export_service2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких сервисов")
    @TmsLink("1523170")
    @Test
    public void exportServicesTest() {
        ExportEntity e = new ExportEntity(simpleService.getId(), simpleService.getVersion());
        ExportEntity e2 = new ExportEntity(simpleService2.getId(), simpleService2.getVersion());
        exportObjectsById("services", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт сервиса по Id")
    @TmsLink("1523167")
    @Test
    public void exportServiceByIdTest() {
        Service service = createService("service_export_test_api");
        exportServiceById(service.getId());
    }

    @DisplayName("Экспорт сервиса по имени")
    @TmsLink("1361360")
    @Test
    public void exportServiceByNameTest() {
        String serviceName = "service_export_by_name_test_api";
        createService(serviceName);
        exportServiceByName(serviceName);
    }
}
