package api.cloud.productCatalog.allowedAction;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.AllowedActionSteps.createAllowedAction;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные Действия")
@DisabledIfEnv("prod")
public class AllowedActionNegativeTest extends Tests {
    @DisplayName("Негативный тест на создание разрешенного действия с неуникальным actionId")
    @TmsLink("1328332")
    @Test
    public void createAllowedActionWithNotUniqActionIdTest() {
        String actionName = "create_allowed_action_with_not_unig_action_id_test_api";
        Action action = Action.builder()
                .name("action_for_allowed_action_test_api")
                .build()
                .createObject();
        AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .actionId(action.getActionId())
                .build()
                .createObject();
        JSONObject json = AllowedAction.builder()
                .name("create2_allowed_action_with_not_unig_action_id_test_api")
                .title("create2_allowed_action_with_not_unig_action_id_test_api")
                .actionId(action.getActionId())
                .build()
                .init()
                .toJson();
        String msg = createAllowedAction(json).extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"non_field_errors\": Поля action должны производить массив с уникальными значениями.", msg);
    }
}
