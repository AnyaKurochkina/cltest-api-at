package api.cloud.productCatalog.forbiddenAction;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ForbiddenActionSteps.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.ProductSteps.importProduct;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ForbiddenActionImportTest extends Tests {
    private static final String PATHNAME = Configure.RESOURCE_PATH + "/json/productCatalog/forbiddenAction/importForbiddenAction.json";

    @DisplayName("Импорт запрещенного действия действия")
    @TmsLink("1092365")
    @Test
    public void importForbiddenActionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/forbiddenAction/importForbiddenAction.json");
        String forbiddenActionName = new JsonPath(data).get("ForbiddenAction.name");
        if (isForbiddenActionExists(forbiddenActionName)) {
            deleteForbiddenActionByName(forbiddenActionName);
        }
        ImportObject importObject = importForbiddenAction(PATHNAME);
        assertEquals(forbiddenActionName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие не существует");
        deleteForbiddenActionByName(forbiddenActionName);
        assertFalse(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие существует");
    }

    @DisplayName("Импорт уже существующего запрещенного действия")
    @TmsLink("1320761")
    @Test
    public void importExistForbiddenActionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/forbiddenAction/importForbiddenAction.json");
        String forbiddenActionName = new JsonPath(data).get("ForbiddenAction.name");
        if (isForbiddenActionExists(forbiddenActionName)) {
            deleteForbiddenActionByName(forbiddenActionName);
        }
        importForbiddenAction(PATHNAME);
        Integer id = getForbiddenActionIdByNameWithMultiSearch(forbiddenActionName);
        String description = "test_test";
        partialUpdateForbiddenAction(id, new JSONObject().put("description", description));
        ForbiddenAction updatedForbiddenAction = getForbiddenActionById(id);
        assertEquals(description, updatedForbiddenAction.getDescription());
        importForbiddenAction(PATHNAME);
        assertNotEquals(updatedForbiddenAction, getForbiddenActionById(id));
        assertTrue(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие не существует");
        deleteForbiddenActionByName(forbiddenActionName);
        assertFalse(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие существует");
    }

    @Test
    @DisplayName("Негативный тест импорт запрещенного действия в другой раздел")
    @TmsLink("1320775")
    public void importForbiddenActionToAnotherSection() {
        String forbiddenActionName = "import_forb_action_for_another_section_test_api";
        ForbiddenAction forbiddenAction = createForbiddenAction(forbiddenActionName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/forbiddenAction/importForbiddenActionAnother.json";
        DataFileHelper.write(filePath, exportForbiddenActionById(String.valueOf(forbiddenAction.getId())).toString());
        deleteForbiddenActionByName(forbiddenActionName);
        importProduct(filePath);
        assertTrue(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие не существует");
        deleteForbiddenActionByName(forbiddenActionName);
        assertFalse(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие существует");
    }

    @DisplayName("Импорт нескольких запрещенных действий")
    @TmsLink("1518484")
    @Test
    public void importMultiForbiddenActionTest() {
        String forbiddenActionName = "import_forbidden_action_test_api";
        if (isForbiddenActionExists(forbiddenActionName)) {
            deleteForbiddenActionByName(forbiddenActionName);
        }
        String forbiddenActionName2 = "import_forbidden_action2_test_api";
        if (isForbiddenActionExists(forbiddenActionName2)) {
            deleteForbiddenActionByName(forbiddenActionName2);
        }
        ForbiddenAction forbiddenAction = createForbiddenAction(forbiddenActionName);
        ForbiddenAction forbiddenAction2 = createForbiddenAction(forbiddenActionName2);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/forbiddenAction/multiForbiddenAction.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/forbiddenAction/multiForbiddenAction2.json";
        DataFileHelper.write(filePath, exportForbiddenActionById(String.valueOf(forbiddenAction.getId())).toString());
        DataFileHelper.write(filePath2, exportForbiddenActionById(String.valueOf(forbiddenAction2.getId())).toString());
        deleteForbiddenActionByName(forbiddenActionName);
        deleteForbiddenActionByName(forbiddenActionName2);
        importObjects("forbidden_actions", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие не существует");
        assertTrue(isForbiddenActionExists(forbiddenActionName2), "Запрещенное действие не существует");
        deleteForbiddenActionByName(forbiddenActionName);
        deleteForbiddenActionByName(forbiddenActionName2);
    }
}
