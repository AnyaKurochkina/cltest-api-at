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
public class ActionIsOnlyForActionFieldTest extends Tests {

    @DisplayName("Проверка дефолтного значения поля is_only_for_api")
    @TmsLink("SOUL-8823")
    @Test
    public void checkIsOnlyForApiFieldByDefaultTest() {
        String actionName = "action_is_only_for_api_default_value_test_api";
        Action action = Action.builder()
                .name(actionName)
                .version("1.0.0")
                .build()
                .createObject();
        assertEquals(false, action.getIsOnlyForApi());
    }

    @DisplayName("Проверка значения поля is_only_for_api")
    @TmsLink("SOUL-8824")
    @Test
    public void checkIsOnlyForApiFieldTest() {
        String actionName = "action_is_only_for_api_test_api";
        Action action = Action.builder()
                .name(actionName)
                .isOnlyForApi(true)
                .version("1.0.0")
                .build()
                .createObject();
        assertEquals(true, action.getIsOnlyForApi());
    }

    @DisplayName("Проверка не версионности поля is_only_for_api")
    @TmsLink("SOUL-8825")
    @Test
    public void updateIsOnlyForApiFieldTest() {
        String version = "1.0.0";
        String actionName = "action_is_only_for_api_update_test_api";
        Action action = Action.builder()
                .name(actionName)
                .isDelayable(true)
                .version(version)
                .build()
                .createObject();
        Action updatedAction = partialUpdateAction(action.getActionId(), new JSONObject().put("is_only_for_api", false))
                .extractAs(Action.class);
        assertEquals(version, updatedAction.getVersion());
        assertEquals(false, updatedAction.getIsOnlyForApi());
    }
}
