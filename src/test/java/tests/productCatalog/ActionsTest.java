package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import org.junit.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.action.getAction.response.GetActionResponse;
import httpModels.productCatalog.action.getActionList.response.ActionResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionsTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("actions/", "productCatalog/actions/createAction.json");
    Action action;

    @Order(1)
    @DisplayName("Создание действия в продуктовом каталоге")
    @TmsLink("640545")
    @Test
    public void createAction() {
        action = Action.builder().actionName("test_object_at2021").build().createObject();
    }

    @Order(2)
    @DisplayName("Получение списка действий")
    @TmsLink("642429")
    @Test
    public void getActionList() {
        assertTrue(productCatalogSteps.getProductObjectList(ActionResponse.class).size() > 0);
    }

    @Order(11)
    @DisplayName("Проверка значения next в запросе на получение списка действий")
    @TmsLink("679025")
    @Test
    public void getMeta() {
        String str = productCatalogSteps.getMeta(ActionResponse.class).getNext();
        if (!(str == null)) {
            assertTrue(str.startsWith("http://dev-kong-service.apps.d0-oscp.corp.dev.vtb/"));
        }
    }

    @Order(3)
    @DisplayName("Проверка существования действия по имени")
    @TmsLink("642432")
    @Test
    public void checkActionExists() {
        assertTrue(productCatalogSteps.isExists(action.getActionName()));
        assertFalse(productCatalogSteps.isExists("NoExistsAction"));
    }

    @Order(4)
    @DisplayName("Импорт действия")
    @TmsLink("642433")
    @Test
    public void importAction() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.json.name");
        productCatalogSteps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        assertTrue(productCatalogSteps.isExists(actionName));
        productCatalogSteps.deleteByName(actionName, ActionResponse.class);
        assertFalse(productCatalogSteps.isExists(actionName));
    }

    @Order(5)
    @DisplayName("Получение действия по Id")
    @TmsLink("642436")
    @Test
    public void getActionById() {
        GetImpl productCatalogGet = productCatalogSteps.getById(action.getActionId(), GetActionResponse.class);
        assertEquals(action.getActionName(), productCatalogGet.getName());
    }

    @Order(6)
    @DisplayName("Негативный тест на получение действия по Id без токена")
    @TmsLink("642485")
    @Test
    public void getActionByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(action.getActionId());
    }

    @Order(7)
    @DisplayName("Копирование действия по Id")
    @TmsLink("642489")
    @Test
    public void copyActionById() {
        String cloneName = action.getActionName() + "-clone";
        productCatalogSteps.copyById(action.getActionId());
        assertTrue(productCatalogSteps.isExists(cloneName));
        productCatalogSteps.deleteByName(cloneName, ActionResponse.class);
        assertFalse(productCatalogSteps.isExists(cloneName));
    }

    @Order(8)
    @DisplayName("Копирование действия по Id и проверка на соответствие полей")
    @TmsLink("642493")
    @Test
    public void copyActionByIdAndFieldCheck() {
        String cloneName = action.getActionName() + "-clone";
        productCatalogSteps.copyById(action.getActionId());
        String cloneId = productCatalogSteps.getProductObjectIdByNameWithMultiSearch(cloneName, ActionResponse.class);
        productCatalogSteps.partialUpdateObject(cloneId, new JSONObject().put("description", "descr_api"));
        GetImpl importedAction = productCatalogSteps.getByIdAndVersion(cloneId, "1.0.1", GetActionResponse.class);
        assertAll(
                () -> assertEquals("1.0.1", importedAction.getVersion()),
                () -> assertEquals("descr_api", importedAction.getDescription())
        );
        assertTrue(productCatalogSteps.isExists(cloneName));
        productCatalogSteps.deleteByName(cloneName, ActionResponse.class);
        assertFalse(productCatalogSteps.isExists(cloneName));
    }

    @Order(60)
    @DisplayName("Негативный тест на копирование действия по Id без токена")
    @TmsLink("642497")
    @Test
    public void copyActionByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(action.getActionId());
    }

    @Order(70)
    @DisplayName("Экспорт действия по Id")
    @TmsLink("642499")
    @Test
    public void exportActionById() {
        productCatalogSteps.exportById(action.getActionId());
    }

    @Order(80)
    @DisplayName("Поиск действия по имени, с использованием multiSearch")
    @TmsLink("642503")
    @Test
    public void searchActionByName() {
        String actionIdWithMultiSearch = productCatalogSteps.getProductObjectIdByNameWithMultiSearch(action.getActionName(), ActionResponse.class);
        assertAll(
                () -> assertNotNull(actionIdWithMultiSearch, String.format("Действие с именем: %s не найден", "test_object_at2021")),
                () -> assertEquals(action.getActionId(), actionIdWithMultiSearch));
    }

    @Order(90)
    @DisplayName("Обновление действия с указанием версии в граничных значениях")
    @TmsLink("642507")
    @Test
    public void updateActionAndGetVersion() {
        Action actionTest = Action.builder().actionName("action_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(actionTest.getActionId(), GetActionResponse.class).getVersion();
        Assertions.assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api4"));
        currentVersion = productCatalogSteps.getById(actionTest.getActionId(), GetActionResponse.class).getVersion();
        Assertions.assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api6"))
                .assertStatus(500);
    }

    @Order(91)
    @DisplayName("Негативный тест на обновление действия по Id без токена")
    @TmsLink("642510")
    @Test
    public void updateActionByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(action.getActionId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(92)
    @DisplayName("Негативный тест на создание действия с двумя параметрами одновременно graph_version_pattern и graph_version")
    @TmsLink("642514")
    @Test
    public void doubleVersionTest() {
        productCatalogSteps.createProductObject(Action.builder().actionName("negative_object").build().init().getTemplate()
                        .set("$.version", "1.1.1")
                        .set("$.graph_version", "1.0.0")
                        .set("$.graph_version_pattern", "1.")
                        .build())
                .assertStatus(500);
    }

    @Order(93)
    @DisplayName("Обновление действия без указания версии, версия должна инкрементироваться")
    @TmsLink("642515")
    @Test
    public void patchTest() {
        String version = productCatalogSteps
                .patchObject(GetActionResponse.class, "test_object_at2021", action.getGraphId(), action.getActionId())
                .getVersion();
        assertEquals("1.0.1", version);
    }

    @Order(94)
    @DisplayName("Негативный тест на обновление действия до той же версии/текущей")
    @TmsLink("642518")
    @Test
    public void sameVersionTest() {
        productCatalogSteps.patchRow(Action.builder().actionName("test_object_at2021").build().init().getTemplate()
                .set("$.version", "1.0.1")
                .build(), action.getActionId()).assertStatus(500);
    }

    @Order(95)
    @DisplayName("Негативный тест на создание действия с существующим именем")
    @TmsLink("642520")
    @Test
    public void createActionWithSameName() {
        productCatalogSteps.createProductObject(productCatalogSteps
                .createJsonObject(action.getActionName())).assertStatus(400);
    }

    @Order(96)
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @TmsLink("642523")
    @Test
    public void createActionWithInvalidCharacters() {
        assertAll("Действие создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("Имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("a&b&c")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("")).assertStatus(400),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject(" ")).assertStatus(400)
        );
    }

    @Order(97)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в действиях")
    @TmsLink("642524")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        String graphVersionCalculated = productCatalogSteps.getById(action.getActionId(), GetActionResponse.class)
                .getGraphVersionCalculated();
        Assertions.assertNotNull(graphVersionCalculated);
    }

    @Order(98)
    @DisplayName("Получение списка действий по фильтрам")
    @TmsLink("642526")
    @Test
    public void getActionListByFilters() {
        assertAll(
                () -> assertTrue(productCatalogSteps
                        .getProductObjectList(ActionResponse.class, "?name=" + action.getActionName()).size() > 0),
                () -> assertTrue(productCatalogSteps
                        .getProductObjectList(ActionResponse.class, "?type=" + "delete").size() > 0),
                () -> assertTrue(productCatalogSteps
                        .getProductObjectList(ActionResponse.class, "?graph_id=" + action.getGraphId()).size() > 0)
        );
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление действия без токена")
    @TmsLink("642528")
    @Test
    public void deleteActionWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(action.getActionId());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление действия")
    @TmsLink("642530")
    @MarkDelete
    public void deleteAction() {
        try (Action action = Action.builder().actionName("test_object_at2021").build().createObjectExclusiveAccess()) {
            action.deleteObject();
        }
    }
}

