package api.cloud.productCatalog.allowedAction;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.getActionById;
import static steps.productCatalog.AllowedActionSteps.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.ProductSteps.importProduct;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные Действия")
@DisabledIfEnv("prod")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllowedActionImportTest extends Tests {
    private static final String PATHNAME = Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/importAllowedAction.json";
    private static final String PATHNAME2 = Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/importAllowedAction2.json";

    @DisplayName("Импорт разрешенного действия")
    @TmsLink("1320447")
    @Test
    public void importAllowedActionTest() {
        JSONObject jsonObject = AllowedAction.builder()
                .title("import_allowed_action_test_api")
                .actionId(createAction().getActionId())
                .build()
                .toJson();
        AllowedAction allowedAction = createAllowedAction(jsonObject).assertStatus(201).extractAs(AllowedAction.class);
        String actionName = allowedAction.getName();
        DataFileHelper.write(PATHNAME, exportAllowedActionById(String.valueOf(allowedAction.getId())).toString());
        deleteAllowedActionByName(actionName);
        ImportObject importObject = importAllowedAction(PATHNAME);
        assertEquals(actionName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isAllowedActionExists(actionName), "Разрешенное действие не существует");
    }

    @DisplayName("Импорт нескольких разрешенный действий")
    @TmsLink("1507972")
    @Test
    public void importMultiAllowedActionTest() {
        AllowedAction allowedAction = createAllowedAction("multi_import_allowed_action_test_api");
        AllowedAction allowedAction2 = createAllowedAction("multi_import_allowed_action2_test_api");
        String allowedActionName = allowedAction.getName();
        String allowedActionName2 = allowedAction2.getName();
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/multiAllowedAction.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/multiAllowedAction2.json";
        DataFileHelper.write(filePath, exportAllowedActionById(String.valueOf(allowedAction.getId())).toString());
        DataFileHelper.write(filePath2, exportAllowedActionById(String.valueOf(allowedAction2.getId())).toString());
        deleteAllowedActionByName(allowedActionName);
        deleteAllowedActionByName(allowedActionName2);
        importObjects("allowed_actions", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isAllowedActionExists(allowedActionName), "Разрешенное действие не существует");
        assertTrue(isAllowedActionExists(allowedActionName2), "Разрешенное действие не существует");
        deleteAllowedActionByName(allowedActionName);
        deleteAllowedActionByName(allowedActionName2);
    }

    @DisplayName("Импорт уже существующего разрешенного действия")
    @TmsLink("1320574")
    @Test
    public void importExistAllowedActionTest() {
        AllowedAction allowedAction = createAllowedAction("import_exist_allowed_action_test_api");
        Action action = getActionById(allowedAction.getActionId());
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/existAllowedActionImport.json";
        DataFileHelper.write(filePath, exportAllowedActionById(String.valueOf(allowedAction.getId())).toString());
        ImportObject importObject = importAllowedAction(filePath);
        DataFileHelper.delete(filePath);
        assertEquals("success", importObject.getStatus());
        assertEquals(String.format("Обновлен объект %s %s:%d", importObject.getModelName(), action.getName(), allowedAction.getId()),
                importObject.getMessages().get(0));
    }

    @Test
    @DisplayName("Негативный тест импорт разрешенного действия в другой раздел")
    @TmsLink("1320584")
    public void importAllowedActionToAnotherSection() {
        AllowedAction allowedAction = createAllowedAction("import_allowed_action_test_api");
        String actionName = allowedAction.getName();
        DataFileHelper.write(PATHNAME2, exportAllowedActionById(String.valueOf(allowedAction.getId())).toString());
        deleteAllowedActionByName(actionName);
        importProduct(PATHNAME2);
        assertTrue(isAllowedActionExists(actionName), "Разрешенное действие не существует");
        deleteAllowedActionByName(actionName);
        assertFalse(isAllowedActionExists(actionName), "Разрешенное действие существует");
    }
}
