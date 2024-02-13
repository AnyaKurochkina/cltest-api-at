package api.cloud.productCatalog.forbiddenAction;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.StringUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.getActionById;
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
    private static final String PATHNAME2 = Configure.RESOURCE_PATH + "/json/productCatalog/forbiddenAction/alreadyExistImportForbiddenAction.json";

    @DisplayName("Импорт запрещенного действия действия")
    @TmsLink("1092365")
    @Test
    public void importForbiddenActionTest() {
        JSONObject jsonObject = ForbiddenAction.builder()
                .title("import_forbidden_action_test_api")
                .actionId(createAction(StringUtils.getRandomStringApi(7)).getId())
                .build()
                .toJson();
        ForbiddenAction forbiddenAction = createForbiddenAction(jsonObject).assertStatus(201).extractAs(ForbiddenAction.class);
        String forbiddenActionName = forbiddenAction.getName();
        DataFileHelper.write(PATHNAME, exportForbiddenActionById(String.valueOf(forbiddenAction.getId())).toString());
        deleteForbiddenActionByName(forbiddenActionName);
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
        String forbiddenActionName = "already_exist_import_forbidden_action_test_api";
        ForbiddenAction forbiddenAction = createForbiddenAction(forbiddenActionName);
        Action action = getActionById(forbiddenAction.getActionId());
        DataFileHelper.write(PATHNAME2, exportForbiddenActionById(String.valueOf(forbiddenAction.getId())).toString());
        JSONObject json = ForbiddenAction.builder()
                .name(forbiddenActionName)
                .actionId(action.getId())
                .build()
                .init()
                .toJson();
        updateForbiddenAction(forbiddenAction.getId(), json);
        ImportObject importObject = importForbiddenAction(PATHNAME2);
        ForbiddenAction getForbiddenAction = getForbiddenActionById(forbiddenAction.getId());
        DataFileHelper.delete(PATHNAME2);
        assertEquals("success", importObject.getStatus());
        assertEquals(String.format("Обновлен объект %s %s:%d,%s", importObject.getModelName(), action.getName(), forbiddenAction.getId(), forbiddenAction.getDirection()),
                importObject.getMessages().get(0));
        assertEquals(forbiddenAction,getForbiddenAction);
    }

    @Test
    @DisplayName("Негативный тест импорт запрещенного действия в другой раздел")
    @TmsLink("1320775")
    public void importForbiddenActionToAnotherSection() {
        ForbiddenAction forbiddenAction = createForbiddenAction("import_forb_action_for_another_section_test_api");
        String forbiddenActionName = forbiddenAction.getName();
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
        ForbiddenAction forbiddenAction = createForbiddenAction("import_forbidden_action_test_api");
        ForbiddenAction forbiddenAction2 = createForbiddenAction("import_forbidden_action2_test_api");
        String forbiddenActionName = forbiddenAction.getName();
        String forbiddenActionName2 = forbiddenAction2.getName();
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
