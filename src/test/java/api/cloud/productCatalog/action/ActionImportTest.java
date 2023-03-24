package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
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
        ImportObject importObject = importAction(RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        assertEquals(actionName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @DisplayName("Импорт нескольких действий")
    @TmsLink("1531556")
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
        String actionName = "import_exist_action_test_api";
        Action action = createAction(actionName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/existActionImport.json";
        DataFileHelper.write(filePath, exportActionById(action.getActionId()).toString());
        ImportObject importObject = importAction(filePath);
        DataFileHelper.delete(filePath);
        assertEquals("error", importObject.getStatus());
        assertEquals( String.format("Error loading dump: Версия \"%s\" %s:%s уже существует. Измените значение версии (\"version_arr: [1, 0, 0]\") у импортируемого объекта и попробуйте снова.",
                        action.getVersion(), importObject.getModelName(), action.getName()),
                importObject.getMessages().get(0));
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
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction2.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        importProduct(RESOURCE_PATH + "/json/productCatalog/actions/importAction2.json");
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");

    }
}
