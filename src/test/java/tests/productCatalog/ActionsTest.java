package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Action.existsAction.response.ExistsActionResponse;
import httpModels.productCatalog.Action.getAction.response.GetActionResponse;
import httpModels.productCatalog.Action.getActionList.response.ActionResponse;
import httpModels.productCatalog.GetImpl;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: действия")
public class ActionsTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();
    Action action;
    private final String productName = "actions/";
    private final String templatePath = "productCatalog/actions/createAction.json";

    @Order(1)
    @DisplayName("Создание действия в продуктовом каталоге")
    @Test
    public void createAction() {
        action = Action.builder().actionName("test_object_at2021").build().createObject();
    }

    @Order(2)
    @DisplayName("Получение списка действий")
    @Test
    public void getActionList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(productName, ActionResponse.class).size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования действия по имени")
    @Test
    public void checkActionExists() {
        Assertions.assertTrue(productCatalogSteps.isExists(productName, action.getActionName(), ExistsActionResponse.class));
        Assertions.assertFalse(productCatalogSteps.isExists(productName, "NoExistsAction", ExistsActionResponse.class));
    }

    @Order(4)
    @DisplayName("Импорт действия")
    @Test
    public void importAction() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.json.name");
        productCatalogSteps.importObject(productName, Configure.RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        Assertions.assertTrue(productCatalogSteps.isExists(productName, actionName, ExistsActionResponse.class));
        productCatalogSteps.deleteByName(productName, actionName, ActionResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, actionName, ExistsActionResponse.class));
    }

    @Order(5)
    @DisplayName("Получение действия по Id")
    @Test
    public void getActionById() {
        GetImpl productCatalogGet = productCatalogSteps.getById(productName, action.getActionId(), GetActionResponse.class);
        Assertions.assertEquals(action.getActionName(), productCatalogGet.getName());
    }

    @Order(6)
    @DisplayName("Негатичный тест на получение действия по Id без токена")
    @Test
    public void getActionByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(productName, action.getActionId(), GetActionResponse.class);
    }

    @Order(7)
    @DisplayName("Копирование действия по Id")
    @Test
    public void copyActionById() {
        String cloneName = action.getActionName() + "-clone";
        productCatalogSteps.copyById(productName, action.getActionId());
        Assertions.assertTrue(productCatalogSteps.isExists(productName, cloneName, ExistsActionResponse.class));
        productCatalogSteps.deleteByName(productName, cloneName, ActionResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, cloneName, ExistsActionResponse.class));
    }

    @Order(8)
    @DisplayName("Негативный тест на копирование действия по Id без токена")
    @Test
    public void copyActionByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(productName, action.getActionId());
    }

    @Order(70)
    @DisplayName("Экспорт действия по Id")
    @Test
    public void exportActionById() {
        productCatalogSteps.exportById(productName, action.getActionId());
    }

    @Order(80)
    @DisplayName("Поиск действия по имени, с использованием multiSearch")
    @Test
    public void searchActionByName() {
        String actionIdWithMultiSearch = productCatalogSteps.getProductObjectIdByNameWithMultiSearch(productName, action.getActionName(), ActionResponse.class);
        assertAll(
                () -> assertNotNull(actionIdWithMultiSearch, String.format("Действие с именем: %s не найден", "test_object_at2021")),
                () -> assertEquals(action.getActionId(), actionIdWithMultiSearch));
    }

    @Order(91)
    @DisplayName("Негативный тест на обновление действия по Id без токена")
    @Test
    public void updateActionByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(productName, action.getActionId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(92)
    @DisplayName("Негативный тест на создание действия с двумя параметрами одновременно graph_version_pattern и graph_version")
    @Test
    public void doubleVersionTest() {
        productCatalogSteps.createProductObject(productName, Action.builder().actionName("negative_object").build().init().getTemplate()
                        .set("$.version", "1.1.1")
                        .set("$.graph_version", "1.0.0")
                        .set("$.graph_version_pattern", "1.")
                        .build())
                .assertStatus(500);
    }

    @Order(93)
    @DisplayName("Обновление действия без указания версии, версия должна инкрементироваться")
    @Test
    public void patchTest() {
        String version = productCatalogSteps
                .patchObject(productName, GetActionResponse.class, "test_object_at2021", action.getGraphId(), action.getActionId())
                .getVersion();
        Assertions.assertEquals("1.1.2", version);
    }

    @Order(94)
    @DisplayName("Негативный тест на обновление действия до той же версии/текущей")
    @Test
    public void sameVersionTest() {
        productCatalogSteps.patchRow(productName, Action.builder().actionName("test_object_at2021").build().init().getTemplate()
                .set("$.version", "1.1.2")
                .build(), action.getActionId()).assertStatus(500);
    }

    @Order(95)
    @DisplayName("Негативный тест на создание действия с существующим именем")
    @Test
    public void createActionWithSameName() {
        productCatalogSteps.createProductObject(productName, productCatalogSteps
                .createJsonObject(action.getActionName(), templatePath)).assertStatus(400);
    }

    @Order(96)
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @Test
    public void createActionWithInvalidCharacters() {
        assertAll("Действие создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("NameWithUppercase", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("имя", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("Имя", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("a&b&c", templatePath)).assertStatus(500)
        );
    }

    @Order(97)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        String graphVersionCalculated = productCatalogSteps.getById(productName, action.getActionId(), GetActionResponse.class)
                .getGraphVersionCalculated();
        Assertions.assertNotNull(graphVersionCalculated);
    }

    @Order(98)
    @DisplayName("Получение списка действий по фильтрам")
    @Test
    public void getActionListByFilters() {
        assertAll(
                () -> assertTrue(productCatalogSteps
                        .getProductObjectList(productName, ActionResponse.class, "?name=" + action.getActionName())
                        .size() > 0),
                () -> assertTrue(productCatalogSteps
                        .getProductObjectList(productName, ActionResponse.class, "?type=" + "delete").size() > 0),
                () -> assertTrue(productCatalogSteps
                        .getProductObjectList(productName, ActionResponse.class, "?graph_id=" + action.getGraphId())
                        .size() > 0)
        );
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление действия без токена")
    @Test
    public void deleteActionWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(productName, action.getActionId());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление действия")
    @MarkDelete
    public void deleteAction() {
        try (Action action = Action.builder().actionName("test_object_at2021").build().createObjectExclusiveAccess()) {
            action.deleteObject();
        }
    }
}

