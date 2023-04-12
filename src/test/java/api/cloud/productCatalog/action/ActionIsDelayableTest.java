package api.cloud.productCatalog.action;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.partialUpdateAction;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionIsDelayableTest extends Tests {

    @DisplayName("Проверка дефолтного значения поля is_delayable")
    @TmsLink("1582261")
    @Test
    public void checkDelayableFieldByDefaultTest() {
        String actionName = "action_is_delayable_default_value_test_api";
        Action action = Action.builder()
                .name(actionName)
                .version("1.0.0")
                .build()
                .createObject();
        assertEquals(false, action.getIsDelayable());
    }

    @DisplayName("Проверка значения поля is_delayable")
    @TmsLink("1582263")
    @Test
    public void checkDelayableFieldTest() {
        String actionName = "action_is_delayable_test_api";
        Action action = Action.builder()
                .name(actionName)
                .isDelayable(true)
                .version("1.0.0")
                .build()
                .createObject();
        assertEquals(true, action.getIsDelayable());
    }

    @DisplayName("Проверка не версионности поля is_delayable")
    @TmsLink("1582266")
    @Test
    public void updateDelayableFieldTest() {
        String version = "1.0.0";
        String actionName = "action_is_delayable_update_test_api";
        Action action = Action.builder()
                .name(actionName)
                .isDelayable(true)
                .version(version)
                .build()
                .createObject();
        Action updatedAction = partialUpdateAction(action.getActionId(), new JSONObject().put("is_delayable", false))
                .extractAs(Action.class);
        assertEquals(version, updatedAction.getVersion());
        assertEquals(false, updatedAction.getIsDelayable());
    }
}
