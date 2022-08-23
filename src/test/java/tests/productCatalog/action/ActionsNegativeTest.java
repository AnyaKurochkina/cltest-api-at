package tests.productCatalog.action;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/actions/",
            "productCatalog/actions/createAction.json");

    @DisplayName("Негативный тест на получение действия по Id без токена")
    @TmsLink("642485")
    @Test
    public void getActionByIdWithOutToken() {
        String actionName = "get_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.getByIdWithOutToken(action.getActionId());
    }

    @DisplayName("Негативный тест на копирование действия по Id без токена")
    @TmsLink("642497")
    @Test
    public void copyActionByIdWithOutToken() {
        String actionName = "copy_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.copyByIdWithOutToken(action.getActionId());
    }

    @DisplayName("Негативный тест на удаление действия без токена")
    @TmsLink("642528")
    @Test
    public void deleteActionWithOutToken() {
        String actionName = "delete_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(action.getActionId());
        assertTrue(steps.isExists(actionName), "Действие существует");
    }

    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @TmsLink("642523")
    @Test
    public void createActionWithInvalidCharacters() {
        Action.builder()
                .actionName("NameWithUppercase")
                .build()
                .negativeCreateRequest(500);
        Action.builder()
                .actionName("nameWithUppercaseInMiddle")
                .build()
                .negativeCreateRequest(500);
        Action.builder()
                .actionName("имя")
                .build()
                .negativeCreateRequest(500);
        Action.builder()
                .actionName("Имя")
                .build()
                .negativeCreateRequest(500);
        Action.builder()
                .actionName("a&b&c")
                .build()
                .negativeCreateRequest(500);
        Action.builder()
                .actionName("")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .actionName(" ")
                .build()
                .negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на создание действия с существующим именем")
    @TmsLink("642520")
    @Test
    public void createActionWithSameName() {
        String actionName = "create_action_with_same_name_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.createProductObject(steps
                .createJsonObject(action.getActionName())).assertStatus(400);
    }

    @DisplayName("Негативный тест на обновление действия по Id без токена")
    @TmsLink("642510")
    @Test
    public void updateActionByIdWithOutToken() {
        String actionName = "update_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(action.getActionId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на создание действия с двумя параметрами одновременно graph_version_pattern и graph_version")
    @TmsLink("642514")
    @Test
    public void doubleVersionTest() {
        Response response = steps.createProductObject(Action.builder()
                        .actionName("negative_object")
                        .build()
                        .init()
                        .getTemplate()
                        .set("$.version", "1.1.1")
                        .set("$.graph_version", "1.0.0")
                        .set("$.graph_version_pattern", "1.")
                        .build())
                .assertStatus(400);
        assertEquals(response.jsonPath().getList("non_field_errors").get(0),
                "You can't use both 'version' and 'version pattern' at same time in the ActionVersionSerializer");
    }

    @DisplayName("Негативный тест на обновление действия до той же версии/текущей")
    @TmsLink("642518")
    @Test
    public void sameVersionTest() {
        String actionName = "action_same_version_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        steps.patchRow(Action.builder().actionName(actionName).build().init().getTemplate()
                .set("$.version", "1.0.1")
                .build(), action.getActionId()).assertStatus(500);
    }

    @Test
    @DisplayName("Негативный тест на передачу невалидного значения current_version в действиях")
    @TmsLink("821961")
    public void setInvalidCurrentVersionAction() {
        String actionName = "invalid_current_version_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.0")
                .build()
                .createObject();
        String actionId = action.getActionId();
        steps.partialUpdateObject(actionId, new JSONObject().put("current_version", "2")).assertStatus(500);
    }
}
