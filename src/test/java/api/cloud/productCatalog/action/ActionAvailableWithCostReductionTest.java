package api.cloud.productCatalog.action;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.partialUpdateAction;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionAvailableWithCostReductionTest extends ActionBaseTest {

    @DisplayName("Проверка дефолтного значения поля available_with_cost_reduction")
    @TmsLink("1639160")
    @Test
    public void checkAvailableWithCostReductionDefaultValueTest() {
        Action action = createAction("action_available_with_cost_reduction_default_value_test_api");
        assertEquals(false, action.getAvailableWithCostReduction());
    }

    @DisplayName("Проверка значения поля available_with_cost_reduction")
    @TmsLink("1639162")
    @Test
    public void checkAvailableWithCostReductionFieldTest() {
        Action action = createAction(createActionModel("action_available_with_cost_reduction_test_api"));
        assertEquals(true, action.getAvailableWithCostReduction());
    }

    @DisplayName("Проверка версионности поля available_with_cost_reduction")
    @TmsLink("1639163")
    @Test
    public void updateAvailableWithCostReductionFieldTest() {
        Action action = createAction(createActionModel("action_available_with_cost_reduction_update_test_api"));
        Action updatedAction = partialUpdateAction(action.getId(), new JSONObject().put("available_with_cost_reduction", false))
                .extractAs(Action.class);
        assertEquals("1.0.1", updatedAction.getVersion());
        assertEquals(false, updatedAction.getAvailableWithCostReduction());
    }

}
