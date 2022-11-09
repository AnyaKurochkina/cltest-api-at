package api.cloud.productCatalog.action;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsNegativeTest extends Tests {

    @DisplayName("Негативный тест на получение действия по Id без токена")
    @TmsLink("642485")
    @Test
    public void getActionByIdWithOutTokenTest() {
        String actionName = "get_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        assertEquals("Unauthorized", getActionByIdWithOutToken(action.getActionId()));
    }

    @DisplayName("Негативный тест на копирование действия по Id без токена")
    @TmsLink("642497")
    @Test
    public void copyActionByIdWithOutTokenTest() {
        String actionName = "copy_action_without_token_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        assertEquals("Unauthorized", copyActionByIdWithOutToken(action.getActionId()));
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
        assertEquals("Unauthorized", deleteActionByIdWithOutToken(action.getActionId()));
        assertTrue(isActionExists(actionName), "Действие существует");
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
        Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        JSONObject json = Action.builder()
                .actionName(actionName)
                .title("title")
                .build()
                .init()
                .toJson();
        String errorMessage = createAction(json).assertStatus(400).jsonPath().getList("name", String.class).get(0);
        assertEquals("action с таким name уже существует.", errorMessage);
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
        assertEquals("Unauthorized", partialUpdateActionWithOutToken(action.getActionId(),
                new JSONObject().put("description", "UpdateDescription")));
    }

    @DisplayName("Негативный тест на создание действия с двумя параметрами одновременно graph_version_pattern и graph_version")
    @TmsLink("642514")
    @Test
    public void doubleVersionTest() {
        Response response = createAction(Action.builder()
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
        partialUpdateAction(action.getActionId(), Action.builder().actionName(actionName).build().init().getTemplate()
                .set("$.version", "1.0.1")
                .build()).assertStatus(500);
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
        partialUpdateAction(actionId, new JSONObject().put("current_version", "2")).assertStatus(500);
    }
}
