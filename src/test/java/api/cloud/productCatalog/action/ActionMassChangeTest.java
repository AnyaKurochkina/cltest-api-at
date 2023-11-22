package api.cloud.productCatalog.action;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
}
