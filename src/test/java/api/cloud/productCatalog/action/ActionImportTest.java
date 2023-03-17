package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.ProductSteps.importProduct;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ActionImportTest extends Tests {

    @DisplayName("Импорт действия")
    @TmsLink("642433")
    @Test
    public void importActionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        importAction(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @DisplayName("Импорт нескольких действий")
    @TmsLink("")
    @Test
    public void importActionsTest() {
        String actionName = "multi_import_action_test_api";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        String actionName2 = "multi_import_action2_test_api";
        if (isActionExists(actionName2)) {
            deleteActionByName(actionName2);
        }
        String graphId = createGraph(RandomStringUtils.randomAlphabetic(10).toLowerCase()).getGraphId();
        Action action = createAction(Action.builder()
                .name(actionName)
                .graphId(graphId)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Action.class);
        Action action2 = createAction(Action.builder()
                .name(actionName2)
                .graphId(graphId)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Action.class);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/multiImportAction.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/actions/multiImportAction2.json";
        DataFileHelper.write(filePath, exportActionById(action.getActionId()).toString());
        DataFileHelper.write(filePath2, exportActionById(action2.getActionId()).toString());
        deleteActionByName(actionName);
        deleteActionByName(actionName2);
        importObjects("actions", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isActionExists(actionName), "Действие не существует");
        assertTrue(isActionExists(actionName2), "Действие не существует");
        deleteActionByName(actionName);
        deleteActionByName(actionName2);
    }

    @DisplayName("Импорт уже существующего действия")
    @TmsLink("1319922")
    @Test
    public void importExistActionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        importAction(RESOURCE_PATH + "/json/productCatalog/actions/importAction.json").assertStatus(200);
        String expectedMsg = "Error loading dump: (Action: import_action_test_api, version = 1.0.2), ['Версия \"1.0.2\" Action:import_action_test_api:import_action_test_api уже существует. Измените значение версии (\"version_arr: [1, 0, 2]\") у импортируемого объекта и попробуйте снова.']";
        String actualMsg = importAction(RESOURCE_PATH + "/json/productCatalog/actions/importAction.json").assertStatus(400)
                .extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, actualMsg);
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @DisplayName("Импорт действия c иконкой")
    @TmsLink("1085391")
    @Test
    public void importActionWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importActionWithIcon.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        importAction(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importActionWithIcon.json");
        String id = getActionIdByNameWithMultiSearch(actionName);
        Action action = getActionById(id);
        assertFalse(action.getIconStoreId().isEmpty());
        assertFalse(action.getIconUrl().isEmpty());
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @Test
    @DisplayName("Негативный тест импорт действия в другой раздел")
    @TmsLink("1319697")
    public void importActionToAnotherSection() {
        String expectedMsg = "Импортируемый объект \"Action\" не соответствует разделу \"Product\"";
        Response response = importProduct(RESOURCE_PATH + "/json/productCatalog/actions/importAction.json").assertStatus(400);
        String error = response.extractAs(ErrorMessage.class).getMessage();
        assertEquals(expectedMsg, error);
    }
}
