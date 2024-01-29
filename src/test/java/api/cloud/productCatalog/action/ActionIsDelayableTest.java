package api.cloud.productCatalog.action;

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
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.partialUpdateAction;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionIsDelayableTest extends ActionBaseTest {

    @DisplayName("Проверка дефолтного значения поля is_delayable")
    @TmsLink("1582261")
    @Test
    public void checkDelayableFieldByDefaultTest() {
        Action actionModel = createActionModel("action_is_delayable_default_value_test_api");
        actionModel.setIsDelayable(null);
        Action action = createAction(actionModel);
        assertEquals(false, action.getIsDelayable(), "Значение поля is_delayable не соответствует ожидаемому");
    }

    @DisplayName("Проверка значения поля is_delayable")
    @TmsLink("1582263")
    @Test
    public void checkDelayableFieldTest() {
        Action actionModel = createActionModel("action_is_delayable_test_api");
        actionModel.setIsDelayable(true);
        Action action = createAction(actionModel);
        assertEquals(true, action.getIsDelayable());
    }

    @DisplayName("Проверка не версионности поля is_delayable")
    @TmsLink("1582266")
    @Test
    public void updateDelayableFieldTest() {
        String version = "1.0.0";
        Action actionModel = createActionModel("action_is_delayable_update_test_api");
        actionModel.setIsDelayable(true);
        actionModel.setVersion(version);
        Action action = createAction(actionModel);
        Action updatedAction = partialUpdateAction(action.getId(), new JSONObject().put("is_delayable", false))
                .extractAs(Action.class);
        assertEquals(version, updatedAction.getVersion());
        assertEquals(false, updatedAction.getIsDelayable());
    }
}
