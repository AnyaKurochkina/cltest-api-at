package api.cloud.productCatalog.allowedAction;

import api.Tests;
import core.helper.Configure;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.AllowedActionSteps.*;
import static steps.productCatalog.ProductSteps.importProduct;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные Действия")
@DisabledIfEnv("prod")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllowedActionImportTest extends Tests {
    private static final String PATHNAME = Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/importAllowedAction.json";

    @DisplayName("Импорт разрешенного действия")
    @TmsLink("1320447")
    @Test
    public void importAllowedActionTest() {
        String data = JsonHelper.getStringFromFile("productCatalog/allowedAction/importAllowedAction.json");
        String allowedActionName = new JsonPath(data).get("AllowedAction.name");
        if (isAllowedActionExists(allowedActionName)) {
            deleteAllowedActionByName(allowedActionName);
        }
        importAllowedAction(PATHNAME).assertStatus(200);
        assertTrue(isAllowedActionExists(allowedActionName), "Разрешенное действие не существует");
        deleteAllowedActionByName(allowedActionName);
        assertFalse(isAllowedActionExists(allowedActionName), "Разрешенное действие существует");
    }

    @DisplayName("Импорт уже существующего разрешенного действия")
    @TmsLink("1320574")
    @Test
    public void importExistAllowedActionTest() {
        String data = JsonHelper.getStringFromFile("productCatalog/allowedAction/importAllowedAction.json");
        String allowedActionName = new JsonPath(data).get("AllowedAction.name");
        if (isAllowedActionExists(allowedActionName)) {
            deleteAllowedActionByName(allowedActionName);
        }
        importAllowedAction(PATHNAME).assertStatus(200);
        AllowedAction allowedActionByName = getAllowedActionByName(allowedActionName);
        String description = "test_test";
        partialUpdateAllowedAction(allowedActionByName.getId(), new JSONObject().put("description", description));
        AllowedAction updatedAllowedAction = getAllowedActionById(allowedActionByName.getId());
        assertEquals(description, updatedAllowedAction.getDescription());
        importAllowedAction(PATHNAME).assertStatus(200);
        assertNotEquals(updatedAllowedAction, getAllowedActionByName(allowedActionName));
        assertTrue(isAllowedActionExists(allowedActionName), "Разрешенное действие не существует");
        deleteAllowedActionByName(allowedActionName);
        assertFalse(isAllowedActionExists(allowedActionName), "Разрешенное действие существует");
    }

    @Test
    @DisplayName("Негативный тест импорт разрешенного действия в другой раздел")
    @TmsLink("1320584")
    public void importAllowedActionToAnotherSection() {
        String expectedMsg = "Импортируемый объект \"AllowedAction\" не соответствует разделу \"Product\"";
        String error = importProduct(PATHNAME).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, error);
    }
}
