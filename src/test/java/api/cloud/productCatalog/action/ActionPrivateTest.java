package api.cloud.productCatalog.action;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionPrivateSteps.*;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionPrivateTest extends Tests {

    @DisplayName("Создание/Получение/Удаление действия в продуктовом каталоге c сервисным токеном")
    @TmsLinks({@TmsLink("1420274"), @TmsLink("1420277"), @TmsLink("1420278")})
    @Test
    public void actionPrivateByIdTest() {
        String actionName = "action_private_test_api";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        JSONObject jsonObject = Action.builder()
                .actionName(actionName)
                .graphId(createGraph(RandomStringUtils.randomAlphabetic(5).toLowerCase()).getGraphId())
                .build()
                .toJson();
        Action action = createActionPrivate(jsonObject);
        String actionId = action.getActionId();
        Action actualAction = getActionPrivateById(actionId);
        assertEquals(action, actualAction);
        deleteActionPrivateById(actionId);
    }

    @DisplayName("Обновление действия c сервисным токеном")
    @TmsLink("1420279")
    @Test
    public void updateActionPrivateTest() {
        String actionName = "action_update_private_test_api";
        Action action = createAction(actionName);
        partialUpdatePrivateAction(action.getActionId(), new JSONObject().put("priority", 1));
        Action updatedAction = getActionById(action.getActionId());
        assertEquals("1.0.1", updatedAction.getVersion(), "Версии не совпадают");
        assertEquals(1, updatedAction.getPriority());
    }

    @DisplayName("Создание/Получение/Удаление действия в продуктовом каталоге c сервисным токеном api/v2")
    @TmsLinks({@TmsLink("1420280"), @TmsLink("1420283"), @TmsLink("1420284")})
    @Test
    public void actionPrivateByNameTest() {
        String actionName = "action_private_v2_test_api";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        JSONObject jsonObject = Action.builder()
                .actionName(actionName)
                .graphId(createGraph(RandomStringUtils.randomAlphabetic(5).toLowerCase()).getGraphId())
                .build()
                .toJson();
        Action action = createActionPrivateV2(jsonObject);
        Action actualAction = getActionPrivateByName(actionName);
        assertEquals(action, actualAction);
        deleteActionPrivateByName(actionName);
    }

    @DisplayName("Обновление действия c сервисным токеном api/v2")
    @TmsLink("1420285")
    @Test
    public void updateActionPrivateByNameTest() {
        String actionName = "action_update_private_by_name_test_api";
        createAction(actionName);
        partialUpdateActionPrivateByName(actionName, new JSONObject().put("priority", 1));
        Action updatedAction = getActionPrivateByName(actionName);
        assertEquals("1.0.1", updatedAction.getVersion(), "Версии не совпадают");
        assertEquals(1, updatedAction.getPriority());
    }
}
