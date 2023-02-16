package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
                .name(actionName)
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
                .name(actionName)
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
                .name(actionName)
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
                .name("NameWithUppercase")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .name("nameWithUppercaseInMiddle")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .name("имя")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .name("Имя")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .name("a&b&c")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .name("")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .name(" ")
                .build()
                .negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на создание действия с существующим именем")
    @TmsLink("642520")
    @Test
    public void createActionWithSameName() {
        String actionName = "create_action_with_same_name_example_test_api";
        Action action = Action.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        String errorMessage = createAction(action.toJson()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("action с таким name уже существует.", errorMessage);
    }

    @DisplayName("Негативный тест на обновление действия по Id без токена")
    @TmsLink("642510")
    @Test
    public void updateActionByIdWithOutToken() {
        String actionName = "update_action_without_token_example_test_api";
        Action action = Action.builder()
                .name(actionName)
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
                .name("negative_object")
                .build()
                .init()
                .getTemplate()
                .set("$.version", "1.1.1")
                .set("$.graph_version", "1.0.0")
                .set("$.graph_version_pattern", "1.")
                .build())
                .assertStatus(400);
        assertEquals("You can't use both 'version' and 'version pattern' at same time in the ActionVersionSerializer",
                response.extractAs(ErrorMessage.class).getMessage());
    }

    @DisplayName("Негативный тест на обновление действия до той же версии/текущей")
    @TmsLink("642518")
    @Test
    public void sameVersionTest() {
        String actionName = "action_same_version_test_api";
        Action action = Action.builder()
                .name(actionName)
                .title(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        String message = partialUpdateAction(action.getActionId(), action.toJson()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Версия %s для %s:%s уже существует", action.getVersion(), action.getName(), action.getTitle()),
                message);
    }

    @Test
    @DisplayName("Негативный тест на передачу невалидного значения current_version в действиях")
    @TmsLink("821961")
    public void setInvalidCurrentVersionAction() {
        String actionName = "invalid_current_version_action_test_api";
        Action action = Action.builder()
                .name(actionName)
                .title(actionName)
                .version("1.0.0")
                .build()
                .createObject();
        String actionId = action.getActionId();
        String message = partialUpdateAction(actionId, new JSONObject().put("current_version", "2")).assertStatus(400)
                .extractAs(ErrorMessage.class).getMessage();
        assertEquals("You must specify version in pattern like \"{num}. | {num}.{num}.\"", message);
    }
}
