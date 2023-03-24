package api.cloud.productCatalog.orgDirection;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.OrgDirectionSteps.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Направления")
@DisabledIfEnv("prod")
public class OrgDirectionImportTest extends Tests {

    @DisplayName("Импорт направления c иконкой")
    @TmsLink("1086532")
    @Test
    public void importOrgDirectionWithIconTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirectionWithIcon.json");
        String name = new JsonPath(data).get("OrgDirection.name");
        if (isOrgDirectionExists(name)) {
            deleteOrgDirectionByName(name);
        }
        importOrgDirection(Configure.RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirectionWithIcon.json");
        String id = getOrgDirectionByNameV2(name).getId();
        OrgDirection orgDirection = getOrgDirectionById(id);
        assertFalse(orgDirection.getIconStoreId().isEmpty());
        assertFalse(orgDirection.getIconUrl().isEmpty());
        assertTrue(isOrgDirectionExists(name), "Направление не существует");
        deleteOrgDirectionByName(name);
        assertFalse(isOrgDirectionExists(name), "Направление существует");
    }

    @DisplayName("Импорт направления")
    @TmsLink("643311")
    @Test
    public void importOrgDirectionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String orgDirectionName = new JsonPath(data).get("OrgDirection.name");
        importOrgDirection(RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirection.json");
        Assertions.assertTrue(isOrgDirectionExists(orgDirectionName));
        deleteOrgDirectionByName(orgDirectionName);
        Assertions.assertFalse(isOrgDirectionExists(orgDirectionName));
    }

    @DisplayName("Импорт нескольких направлений")
    @TmsLink("1520313")
    @Test
    public void importMultiOrgDirectionTest() {
        String orgDirectionName = "multi_import_org_direction_test_api";
        if (isOrgDirectionExists(orgDirectionName)) {
            deleteOrgDirectionByName(orgDirectionName);
        }
        String orgDirectionName2 = "multi_import_org_direction2_test_api";
        if (isOrgDirectionExists(orgDirectionName2)) {
            deleteOrgDirectionByName(orgDirectionName2);
        }
        OrgDirection orgDirection = createOrgDirectionByName(orgDirectionName);
        OrgDirection orgDirection2 = createOrgDirectionByName(orgDirectionName2);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/orgDirection/multiOrgDirection.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/orgDirection/multiOrgDirection2.json";
        DataFileHelper.write(filePath, exportOrgDirectionById(orgDirection.getId()).toString());
        DataFileHelper.write(filePath2, exportOrgDirectionById(orgDirection2.getId()).toString());
        deleteOrgDirectionByName(orgDirectionName);
        deleteOrgDirectionByName(orgDirectionName2);
        importObjects("org_direction", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isOrgDirectionExists(orgDirectionName), "Направление не существует");
        assertTrue(isOrgDirectionExists(orgDirectionName2), "Направление не существует");
        deleteOrgDirectionByName(orgDirectionName);
        deleteOrgDirectionByName(orgDirectionName2);
    }
}
