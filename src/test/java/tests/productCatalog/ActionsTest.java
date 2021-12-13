package tests.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Action.getAction.response.GetActionResponse;
import httpModels.productCatalog.patchActions.response.PatchActionResponse;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ActionsSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
public class ActionsTest extends Tests {
    ActionsSteps actionsSteps = new ActionsSteps();
    Action action;

    @Order(1)
    @DisplayName("Создание экшена в продуктовом каталоге")
    @Test
    public void createAction() {
        action = Action.builder().actionName("test_object_at2021").build().createObject();
    }

    @Order(2)
    @DisplayName("Получение списка действий")
    @Test
    public void getActionList() {
        Assertions.assertTrue(actionsSteps.getActionList().size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования действия по имени")
    @Test
    public void checkActionExists() {
        Assertions.assertTrue(actionsSteps.isActionExists(action.getActionName()));
        Assertions.assertFalse(actionsSteps.isActionExists("NoExistsAction"));
    }

    @Order(4)
    @DisplayName("Импорт действия")
    @Test
    public void importAction() {
        String data = new JsonHelper().getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.json.name");
        actionsSteps.importAction(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        Assertions.assertTrue(actionsSteps.isActionExists(actionName));
        actionsSteps.deleteActionByName(actionName);
        Assertions.assertFalse(actionsSteps.isActionExists(actionName));
    }

    @Order(5)
    @DisplayName("Получение действия по Id")
    @Test
    public void getActionById() {
        GetActionResponse getActionResponse = actionsSteps.getActionById(action.getActionId());
        Assertions.assertEquals(action.getActionName(), getActionResponse.getName());
    }

    @Order(6)
    @DisplayName("Копирование действия по Id")
    @Test
    public void copyActionById() {
        String cloneName = action.getActionName() + "-clone";
        actionsSteps.copyActionById(action.getActionId());
        Assertions.assertTrue(actionsSteps.isActionExists(cloneName));
        actionsSteps.deleteActionByName(cloneName);
        Assertions.assertFalse(actionsSteps.isActionExists(cloneName));
    }

    @Order(7)
    @DisplayName("Экспорт действия по Id")
    @Test
    public void exportActionById() {
        actionsSteps.exportActionById(action.getActionId());
    }

    @Order(8)
    @DisplayName("Поиск экшена по имени, с использованием multiSearch")
    @Test
    public void searchActionByName() {
        String actionIdWithMultiSearch = actionsSteps.getActionIdByNameWithMultiSearch(action.getActionName());
        assertAll(
                () -> assertNotNull(actionIdWithMultiSearch, String.format("Экшен с именем: %s не найден", "test_object_at2021")),
                () -> assertEquals(action.getActionId(), actionIdWithMultiSearch));
    }

    @Order(9)
    @DisplayName("Негативный тест на создание экшена с двумя параметрами одновременно graph_version_pattern и graph_version")
    @Test
    public void doubleVersionTest() {
        Http.Response resp = actionsSteps.createAction(Action.builder().actionName("negative_object").build().init().getTemplate()
                        .set("$.version", "1.1.1")
                        .set("$.graph_version", "1.0.0")
                        .set("$.graph_version_pattern", "1.")
                        .build())
                .assertStatus(500);
    }

    @Order(10)
    @DisplayName("Обновление экшена без указания версии, вресия должна инкрементироваться")
    @Test
    public void patchTest() {
        PatchActionResponse patchActionResponse = actionsSteps.patchAction("test_object_at2021", action.getGraphId(), action.getActionId());
        Assertions.assertEquals("1.1.2", patchActionResponse.getLastVersion());
    }

    @Order(11)
    @DisplayName("Негативный тест на обновление экшена до той же версии/текущей")
    @Test
    public void sameVersionTest() {
        actionsSteps.patchActionRow(Action.builder().actionName("test_object_at2021").build().init().getTemplate()
                .set("$.version", "1.1.2")
                .build(), action.getActionId()).assertStatus(404);
    }

    @Order(12)
    @DisplayName("Негативный тест на создание действия с существующим именем")
    @Test
    public void createActionWithSameName() {
        actionsSteps.createAction(actionsSteps.createJsonObject(action.getActionName())).assertStatus(400);
    }

    @Order(13)
    @DisplayName("Негативный тест на создание действия с именем содержащее латинскую букву в верхнем регистре")
    @Test
    public void createActionWithEngWordInUppercase() {
        JSONObject nameWithUppercase = actionsSteps.createJsonObject("NameWithUppercase");
        actionsSteps.createAction(nameWithUppercase).assertStatus(400);
    }

    @Order(14)
    @DisplayName("Негативный тест на создание действия с именем содержащее латинскую букву в верхнем регистре в середине имени")
    @Test
    public void createActionWithEngWordInUppercaseInMiddle() {
        String objectName = "nameWithUppercaseInMiddle";
        JSONObject nameWithUppercaseInMiddle = actionsSteps.createJsonObject(objectName);
        actionsSteps.createAction(nameWithUppercaseInMiddle).assertStatus(400);
    }

    @Order(15)
    @DisplayName("Негативный тест на создание действия с именем на русском языке в нижнем регистре")
    @Test
    public void createActionWithRusWordInLowCase() {
        String objectName = "имя";
        JSONObject nameWithRusWordInLowCase = actionsSteps.createJsonObject(objectName);
        actionsSteps.createAction(nameWithRusWordInLowCase).assertStatus(400);
    }

    @Order(16)
    @DisplayName("Негативный тест на создание действия с именем нарусском языке с буквой в верхнем регистре")
    @Test
    public void createActionWithRusWordInUppercase() {
        JSONObject nameWithRusWordInUppercase = actionsSteps.createJsonObject("Имя");
        actionsSteps.createAction(nameWithRusWordInUppercase).assertStatus(400);
    }

    @Order(17)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetActionResponse getActionResponse = actionsSteps.getActionById(action.getActionId());
        Assertions.assertNotNull(getActionResponse.getGraphVersionCalculated());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление экшена")
    @MarkDelete
    public void deleteAction() {
        try (Action action = Action.builder().actionName("test_object_at2021").build().createObjectExclusiveAccess()) {
            action.deleteObject();
        }
    }
}

