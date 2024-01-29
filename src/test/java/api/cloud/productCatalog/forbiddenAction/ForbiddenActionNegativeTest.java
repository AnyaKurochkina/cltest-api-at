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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ForbiddenActionSteps.createForbiddenAction;
import static steps.productCatalog.ForbiddenActionSteps.partialUpdateForbiddenAction;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionNegativeTest extends Tests {

    @DisplayName("Негативный тест на создание запрещенного действия с неуникальной связкой знайчений action и direction")
    @TmsLink("1328275")
    @Test
    public void createForbiddenActionWithNotUnigActionDirectionTest() {
        Action action = createAction("action_for_non_unique_action_direction_test_api");
        ForbiddenAction.builder()
                .title("create_forbidden_action_with_not_unig_action_direction_test_api")
                .direction("parent_to_child")
                .actionId(action.getId())
                .build()
                .createObject();
        JSONObject json = ForbiddenAction.builder()
                .title("create_second_forbidden_action_with_not_unig_action_direction_test_api")
                .direction("parent_to_child")
                .actionId(action.getId())
                .build()
                .init()
                .toJson();
        String msg = createForbiddenAction(json).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"non_field_errors\": Поля action, direction должны производить массив с уникальными значениями.", msg);
    }

    @DisplayName("Негативный тест на редактирование имени запрещенного действия")
    @TmsLink("")
    @Test
    public void editForbiddenActionNameTest() {
        String forbiddenActionName = "edit_forbidden_action_name_test_api";
        ForbiddenAction action = createForbiddenAction("create_allowed_action_for_edit_name_api_test");
        partialUpdateForbiddenAction(action.getId(), new JSONObject().put("name", forbiddenActionName));
        assertNotEquals(action.getName(), forbiddenActionName);
    }
}
