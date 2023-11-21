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

import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.massChangeActionParam;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionMassChangeTest extends Tests {

    @DisplayName("")
    @TmsLink("")
    @Test
    public void massChangeActionTest() {
        Action p1 = createAction("action1_mass_change_test_api");
        Action p2 = createAction("action2_mass_change_test_api");
        Action p3 = createAction("action3_mass_change_test_api");
        Action p4 = createAction("action4_mass_change_test_api");
        Action p5 = createAction("action5_mass_change_test_api");
        massChangeActionParam(Arrays.asList(p1.getActionId(), p2.getActionId(), p3.getActionId(), p4.getActionId(),
                        p5.getActionId()),true);
    }
}
