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
public class ActionIsOnlyForActionFieldTest extends ActionBaseTest {

    @DisplayName("Проверка дефолтного значения поля is_only_for_api")
    @TmsLink("SOUL-8823")
    @Test
    public void checkIsOnlyForApiFieldByDefaultTest() {
        Action actionModel = createActionModel("action_is_only_for_api_default_value_test_api");
        actionModel.setIsOnlyForApi(null);
        Action action = createAction(actionModel);
        assertEquals(false, action.getIsOnlyForApi(), "Значение поля is_only_for_api не соответствует ожидаемому");
    }

    @DisplayName("Проверка значения поля is_only_for_api")
    @TmsLink("SOUL-8824")
    @Test
    public void checkIsOnlyForApiFieldTest() {
        Action actionModel = createActionModel("action_is_only_for_api_test_api");
        actionModel.setIsOnlyForApi(true);
        Action action = createAction(actionModel);
        assertEquals(true, action.getIsOnlyForApi(), "Значение поля is_only_for_api не соответствует ожидаемому");
    }

    @DisplayName("Проверка не версионности поля is_only_for_api")
    @TmsLink("SOUL-8825")
    @Test
    public void updateIsOnlyForApiFieldTest() {
        String version = "1.0.0";
        Action actionModel = createActionModel("action_is_only_for_api_update_test_api");
        actionModel.setIsOnlyForApi(true);
        Action action = createAction(actionModel);
        Action updatedAction = partialUpdateAction(action.getId(), new JSONObject().put("is_only_for_api", false))
                .extractAs(Action.class);
        assertEquals(version, updatedAction.getVersion(), "Версия изменилась");
        assertEquals(false, updatedAction.getIsOnlyForApi(), "Значение поля is_only_for_api не соответствует ожидаемому");
    }
}
