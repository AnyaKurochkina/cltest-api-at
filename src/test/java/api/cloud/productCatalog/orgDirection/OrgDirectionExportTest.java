package api.cloud.productCatalog.orgDirection;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.OrgDirectionSteps.createOrgDirectionByName;
import static steps.productCatalog.OrgDirectionSteps.exportOrgDirectionById;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Направления")
@DisabledIfEnv("prod")
public class OrgDirectionExportTest extends Tests {
    private static OrgDirection orgDirection;
    private static OrgDirection orgDirection1;

    @BeforeAll
    public static void setUp() {
        orgDirection = createOrgDirectionByName("export_org_direction1_test_api");
        orgDirection1 = createOrgDirectionByName("export_org_direction2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких направлений")
    @TmsLink("1522880")
    @Test
    public void multiExportOrgDirectionTest() {
        ExportEntity e = new ExportEntity(orgDirection.getId());
        ExportEntity e2 = new ExportEntity(orgDirection1.getId());
        exportObjectsById("org_direction", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт направления по Id")
    @TmsLink("643334")
    @Test
    public void exportOrgDirectionByIdTest() {
        OrgDirection orgDirection = createOrgDirectionByName("export_org_direction_test_api");
        exportOrgDirectionById(orgDirection.getId());
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте направления")
    @TmsLink("SOUL-")
    @Test
    public void checkExportedObjectsFieldOrgDirectionTest() {
        String orgDirectionName = "org_direction_exported_objects_test_api";
        OrgDirection orgDirection = createOrgDirectionByName(orgDirectionName);
        Response response = exportOrgDirectionById(orgDirection.getId());
        LinkedHashMap r = response.jsonPath().get("exported_objects.OrgDirection.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(orgDirection.getName(), jsonObject.get("name").toString());
    }
}
