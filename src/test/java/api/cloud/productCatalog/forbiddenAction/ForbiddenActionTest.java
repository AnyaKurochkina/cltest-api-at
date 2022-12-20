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
import static steps.productCatalog.ForbiddenActionSteps.getForbiddenActionById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionTest extends Tests {

    @DisplayName("Создание запрещенного действия")
    @TmsLink("1144654")
    @Test
    public void createForbiddenActionTest() {
        ForbiddenAction forbiddenAction = ForbiddenAction.builder()
                .name("create_forbidden_action_test_api")
                .title("create_forbidden_action_test_api")
                .build()
                .createObject();
        ForbiddenAction getForbiddenAction = getForbiddenActionById(forbiddenAction.getId());
        assertEquals(forbiddenAction, getForbiddenAction);
    }

    @DisplayName("Создание запрещенного действия c запретом самому себе")
    @TmsLink("1277044")
    @Test
    public void createForbiddenActionWithParentToSelf() {
        Action action = Action.builder()
                .actionName("action_for_forbidden_action_api_test")
                .title("api_test")
                .build()
                .createObject();
        JSONObject json = ForbiddenAction.builder()
                .name("create_forbidden_action_with_parent_to_self_test_api")
                .title("create_forbidden_action_with_parent_to_self_test_api")
                .actionId(action.getActionId())
                .direction("parent_to_self")
                .build()
                .init()
                .toJson();
        String message = createForbiddenAction(json).assertStatus(400).jsonPath().getList("", String.class).get(0);
        assertEquals(String.format("['This direction (parent_to_self) is depricated. Please, use another option. (%s:%s:None,parent_to_self)']",
                action.getActionName(), action.getTitle()), message);

    }
}
