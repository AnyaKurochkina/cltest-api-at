package api.cloud.productCatalog.action;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static core.helper.StringUtils.format;
import static core.helper.StringUtils.getRandomStringApi;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionMassChangeTest extends Tests {

    @DisplayName("Массовое изменение параметра is_for_items у действий")
    @TmsLink("SOUL-8338")
    @Test
    public void massChangeActionTest() {
        Action action1 = createAction("action1_mass_change_test_api");
        Action action2 = createAction("action2_mass_change_test_api");
        Action action3 = createAction("action3_mass_change_test_api");
        Action action4 = createAction("action4_mass_change_test_api");
        Action action5 = createAction("action5_mass_change_test_api");
        List<String> actionIdList = Arrays.asList(action1.getActionId(), action2.getActionId(), action3.getActionId(),
                action4.getActionId(), action5.getActionId());
        massChangeActionParam(actionIdList, false);
        actionIdList.forEach(x -> assertFalse(getActionById(x).getIsForItems()));
        massChangeActionParam(actionIdList, true);
        actionIdList.forEach(x -> assertTrue(getActionById(x).getIsForItems()));
    }

    @DisplayName("Массовое изменение параметра is_for_items у несуществующего действия")
    @TmsLink("SOUL-8674")
    @Test
    public void massChangeNotExistActionTest() {
        Action action1 = createAction(getRandomStringApi(6));
        String notExistActionUUID = UUID.randomUUID().toString();
        List<String> actionIdList = Arrays.asList(action1.getActionId(), notExistActionUUID);

        String response = uncheckedMassChangeActionParam(actionIdList, true).assertStatus(400).toString();
        String updatedResponse = response.replace("is_for_items:True", "is_for_items_true");
        String errorMessage = JsonPath.from(updatedResponse).get("is_for_items_true[1].error");

        assertEquals(format("Object Action with id={} does not exists", notExistActionUUID), errorMessage);
    }
}
