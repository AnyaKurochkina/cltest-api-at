package api.cloud.productCatalog.forbiddenAction;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ForbiddenActionSteps.createForbiddenAction;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionNegativeTest extends Tests {

    @DisplayName("Негативный тест на создание запрещенного действия с неуникальной связкой знайчений action и direction")
    @TmsLink("1328275")
    @Test
    public void createForbiddenActionWithNotUnigActionDirectionTest() {
        Action action = Action.builder()
                .actionName("action_for_non_unique_action_direction_test_api")
                .build()
                .createObject();
        ForbiddenAction.builder()
                .name("create_forbidden_action_with_not_unig_action_direction_test_api")
                .title("create_forbidden_action_with_not_unig_action_direction_test_api")
                .direction("parent_to_child")
                .actionId(action.getActionId())
                .build()
                .createObject();
        JSONObject json = ForbiddenAction.builder()
                .name("create_second_forbidden_action_with_not_unig_action_direction_test_api")
                .title("create_second_forbidden_action_with_not_unig_action_direction_test_api")
                .direction("parent_to_child")
                .actionId(action.getActionId())
                .build()
                .init()
                .toJson();
        String msg = createForbiddenAction(json).jsonPath().getList("non_field_errors", String.class).get(0);
        assertEquals("Поля action, direction должны производить массив с уникальными значениями.", msg);
    }
}
