package api.cloud.productCatalog.allowedAction;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
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

    @DisplayName("Импорт разрешенного действия")
    @TmsLink("1320447")
    @Test
    public void importAllowedActionTest() {
        String data = JsonHelper.getStringFromFile("productCatalog/allowedAction/importAllowedAction.json");
        String allowedActionName = new JsonPath(data).get("AllowedAction.name");
        if (isAllowedActionExists(allowedActionName)) {
            deleteAllowedActionByName(allowedActionName);
        }
        ImportObject importObject = importAllowedAction(PATHNAME);
        assertEquals(allowedActionName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isAllowedActionExists(allowedActionName), "Разрешенное действие не существует");
        deleteAllowedActionByName(allowedActionName);
        assertFalse(isAllowedActionExists(allowedActionName), "Разрешенное действие существует");
    }

    @DisplayName("Импорт нескольких разрешенный действий")
    @TmsLink("1507972")
    @Test
    public void importMultiAllowedActionTest() {
        String allowedActionName = "import_allowed_action_test_api";
        if (isAllowedActionExists(allowedActionName)) {
            deleteAllowedActionByName(allowedActionName);
        }
        String allowedActionName2 = "import_allowed_action2_test_api";
        if (isAllowedActionExists(allowedActionName2)) {
            deleteAllowedActionByName(allowedActionName2);
        }
        AllowedAction allowedAction = createAllowedAction(allowedActionName);
        AllowedAction allowedAction2 = createAllowedAction(allowedActionName2);
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
        String allowedActionName = "import_exist_allowed_action_test_api";
        AllowedAction allowedAction = createAllowedAction(allowedActionName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/existAllowedActionImport.json";
        DataFileHelper.write(filePath, exportAllowedActionById(String.valueOf(allowedAction.getId())).toString());
        ImportObject importObject = importAllowedAction(filePath);
        DataFileHelper.delete(filePath);
        assertEquals("success", importObject.getStatus());
        assertEquals( String.format("Обновлен объект %s %s:%d", importObject.getModelName(), allowedAction.getName(), allowedAction.getId()),
                importObject.getMessages().get(0));
    }

    @Test
    @DisplayName("Негативный тест импорт разрешенного действия в другой раздел")
    @TmsLink("1320584")
    public void importAllowedActionToAnotherSection() {
        String data = JsonHelper.getStringFromFile("productCatalog/allowedAction/importAllowedAction2.json");
        String allowedActionName = new JsonPath(data).get("AllowedAction.name");
        if (isAllowedActionExists(allowedActionName)) {
            deleteAllowedActionByName(allowedActionName);
        }
        importProduct(Configure.RESOURCE_PATH + "/json/productCatalog/allowedAction/importAllowedAction2.json");
        assertTrue(isAllowedActionExists(allowedActionName), "Разрешенное действие не существует");
        deleteAllowedActionByName(allowedActionName);
        assertFalse(isAllowedActionExists(allowedActionName), "Разрешенное действие существует");
    }
}
