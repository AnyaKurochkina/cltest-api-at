package api.cloud.productCatalog.action;

import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.getActionById;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionCreateWithEventTypeProviderTest extends ActionBaseTest {

    @DisplayName("Создание action c event_type_provider из списка справочника")
    @TmsLink("1267448")
    @Test
    public void createActionWithEventProviderTest() {
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(new EventTypeProvider("vm", "vsphere"));
        String actionName = "create_action_with_exist_event_type_provider_test_api";
        Action actionModel = createActionModel(actionName);
        actionModel.setEventTypeProvider(expectedEventTypeProviderList);
        Action action = createAction(actionModel);
        List<EventTypeProvider> actualEventTypeProviderList = getActionById(action.getId()).getEventTypeProvider();
        assertEquals(expectedEventTypeProviderList, actualEventTypeProviderList);
    }

    @DisplayName("Негативный тест на создание action c event_type_provider не из списка справочника")
    @TmsLink("1267480")
    @Test
    public void createActionWithNotExistEventProviderTest() {
        EventTypeProvider eventTypeProvider = new EventTypeProvider("test", "test");
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(eventTypeProvider);
        Action actionModel = createActionModel("create_action_with_not_exist_event_type_provider_test_api");
        actionModel.setEventTypeProvider(expectedEventTypeProviderList);
        AssertResponse.run(() -> createAction(actionModel.toJson())).status(400)
                .responseContains(String.format("Validation error (event_type_provider): String 1: Wrong value (%s) of event_type", eventTypeProvider.getEvent_type()));
    }
}
