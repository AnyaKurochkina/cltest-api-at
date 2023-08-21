package api.cloud.productCatalog.forbiddenAction;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ForbiddenActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionTest extends Tests {

    @DisplayName("Создание запрещенного действия")
    @TmsLink("1144654")
    @Test
    public void createForbiddenActionTest() {
        ForbiddenAction forbiddenAction = createForbiddenAction("create_forbidden_action_test_api");
        ForbiddenAction getForbiddenAction = getForbiddenActionById(forbiddenAction.getId());
        assertEquals(forbiddenAction, getForbiddenAction);
    }

    @DisplayName("Создание запрещенного действия c запретом самому себе")
    @TmsLink("1277044")
    @Test
    public void createForbiddenActionWithParentToSelf() {
        Action action = Action.builder()
                .name("action_for_forbidden_action_api_test")
                .title("api_test")
                .build()
                .createObject();
        JSONObject json = ForbiddenAction.builder()
                .title("create_forbidden_action_with_parent_to_self_test_api")
                .actionId(action.getActionId())
                .direction("parent_to_self")
                .build()
                .init()
                .toJson();
        String message = createForbiddenAction(json).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Значение parent_to_self устарело. Пожалуйста, выберите другое.", message);
    }

    @DisplayName("Проверка имени forbidden action после создания с direction = parent_to_child")
    @TmsLink("6864")
    @Test
    public void createForbiddenActionWithDirectionParentToChildTest() {
        Action action = createAction();
        JSONObject json = ForbiddenAction.builder()
                .title("create_forbidden_action_with_parent_to_child_test_api")
                .actionId(action.getActionId())
                .direction("parent_to_child")
                .build()
                .init()
                .toJson();
        ForbiddenAction forbiddenAction = createForbiddenAction(json).assertStatus(201).extractAs(ForbiddenAction.class);
        assertEquals(action.getName() + "__" + forbiddenAction.getDirection(), forbiddenAction.getName());
    }

    @DisplayName("Проверка имени forbidden action после создания с direction = child_to_parent")
    @TmsLink("6865")
    @Test
    public void createForbiddenActionWithDirectionChildToParentTest() {
        Action action = createAction();
        JSONObject json = ForbiddenAction.builder()
                .title("create_forbidden_action_with_child_to_parent_test_api")
                .actionId(action.getActionId())
                .direction("child_to_parent")
                .build()
                .init()
                .toJson();
        ForbiddenAction forbiddenAction = createForbiddenAction(json).assertStatus(201).extractAs(ForbiddenAction.class);
        assertEquals(action.getName() + "__" + forbiddenAction.getDirection(), forbiddenAction.getName());
    }

    @DisplayName("Копирование forbidden action по id")
    @TmsLink("SOUL-7075")
    @Test
    public void copyByIdForbiddenActionTest() {
        ForbiddenAction forbiddenAction = ForbiddenAction.builder()
                .title("copy_by_id_forbidden_action_test_api")
                .build()
                .createObject();
        String direction = "child_to_parent";
        Action action1 = createAction();
        ForbiddenAction copiedForbiddenAction = copyForbiddenActionById(forbiddenAction.getId(), new JSONObject()
                .put("action_id", action1.getActionId())
                .put("direction", direction));
        assertTrue(isForbiddenActionExists(copiedForbiddenAction.getName()));
        assertEquals(action1.getName() + "__" + direction, copiedForbiddenAction.getName());
    }
}
