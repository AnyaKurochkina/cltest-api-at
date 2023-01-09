package api.cloud.productCatalog.forbiddenAction;

import api.Tests;
import core.helper.Configure;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ForbiddenActionSteps.*;
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
        importForbiddenAction(PATHNAME);
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
        importForbiddenAction(PATHNAME).assertStatus(200);
        Integer id = getForbiddenActionIdByNameWithMultiSearch(forbiddenActionName);
        String description = "test_test";
        partialUpdateForbiddenAction(id, new JSONObject().put("description", description));
        ForbiddenAction updatedForbiddenAction = getForbiddenActionById(id);
        assertEquals(description, updatedForbiddenAction.getDescription());
        importForbiddenAction(PATHNAME).assertStatus(200);
        assertNotEquals(updatedForbiddenAction, getForbiddenActionById(id));
        assertTrue(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие не существует");
        deleteForbiddenActionByName(forbiddenActionName);
        assertFalse(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие существует");
    }

    @Test
    @DisplayName("Негативный тест импорт запрещенного действия в другой раздел")
    @TmsLink("1320775")
    public void importForbiddenActionToAnotherSection() {
        String expectedMsg = "Импортируемый объект \"ForbiddenAction\" не соответствует разделу \"Product\"";
        String error = importProduct(PATHNAME).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, error);
    }
}
