package api.cloud.productCatalog.allowedAction;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.AllowedActionSteps.createAllowedAction;
import static steps.productCatalog.AllowedActionSteps.getAllowedActionById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные Действия")
@DisabledIfEnv("prod")
public class AllowedActionCreateWithEventTypeProviderTest extends Tests {

    @DisplayName("Создание allowed_action c event_type_provider из списка справочника")
    @TmsLink("1267874")
    @Test
    public void createAllowedActionWithEventProviderTest() {
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(new EventTypeProvider("vm", "vsphere"));
        AllowedAction action = AllowedAction.builder()
                .title("create_allowed_action_with_event_provider_test_api")
                .eventTypeProvider(expectedEventTypeProviderList)
                .build()
                .createObject();
        List<EventTypeProvider> actualEventTypeProviderList = getAllowedActionById(action.getId()).getEventTypeProvider();
        assertEquals(expectedEventTypeProviderList, actualEventTypeProviderList);
    }

    @DisplayName("Негативный тест на создание allowed_action c event_type_provider не из списка справочника")
    @TmsLink("1267877")
    @Test
    public void createActionWithNotExistEventProviderTest() {
        EventTypeProvider eventTypeProvider = new EventTypeProvider("test", "test");
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(eventTypeProvider);
        JSONObject json = AllowedAction.builder()
                .eventTypeProvider(expectedEventTypeProviderList)
                .build()
                .init()
                .toJson();
        String message = createAllowedAction(json).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Validation error (event_type_provider): String 1: Wrong value (%s) of event_type", eventTypeProvider.getEvent_type()),
                message);
    }
}
