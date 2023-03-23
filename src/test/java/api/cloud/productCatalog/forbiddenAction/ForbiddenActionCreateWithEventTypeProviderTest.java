package api.cloud.productCatalog.forbiddenAction;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ForbiddenActionSteps.createForbiddenAction;
import static steps.productCatalog.ForbiddenActionSteps.getForbiddenActionById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionCreateWithEventTypeProviderTest extends Tests {

    @DisplayName("Создание forbidden_action c event_type_provider из списка справочника")
    @TmsLink("1267870")
    @Test
    public void createForbiddenActionWithEventProviderTest() {
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(new EventTypeProvider("vm", "vsphere"));
        String actionName = "create_forbidden_action_with_exist_event_type_provider_test_api";
        ForbiddenAction action = ForbiddenAction.builder()
                .name(actionName)
                .eventTypeProvider(expectedEventTypeProviderList)
                .build()
                .createObject();
        List<EventTypeProvider> actualEventTypeProviderList = getForbiddenActionById(action.getId()).getEventTypeProvider();
        assertEquals(expectedEventTypeProviderList, actualEventTypeProviderList);
    }

    @DisplayName("Негативный тест на создание forbidden_action c event_type_provider не из списка справочника")
    @TmsLink("1267872")
    @Test
    public void createForbiddenActionWithNotExistEventProviderTest() {
        EventTypeProvider eventTypeProvider = new EventTypeProvider("test", "test");
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(eventTypeProvider);
        String actionName = "create_forbidden_action_with_not_exist_event_type_provider_test_api";
        JSONObject jsonObject = ForbiddenAction.builder()
                .name(actionName)
                .eventTypeProvider(expectedEventTypeProviderList)
                .build()
                .init()
                .toJson();
        String message = createForbiddenAction(jsonObject).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Ошибка валидации (event_type_provider): String 1: Wrong value (%s) of event_type", eventTypeProvider.getEvent_type()),
                message);

    }
}
