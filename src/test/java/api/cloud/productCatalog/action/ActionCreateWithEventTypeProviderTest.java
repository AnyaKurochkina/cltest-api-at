package tests.productCatalog.action;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.action.Action;
import models.productCatalog.action.EventTypeProvider;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.getActionById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionCreateWithEventTypeProviderTest extends Tests {

    @DisplayName("Создание action c event_type_provider из списка справочника")
    @TmsLink("1267448")
    @Test
    public void createActionWithEventProviderTest() {
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(new EventTypeProvider("vm", "vsphere"));
        String actionName = "create_action_with_exist_event_type_provider_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .eventTypeProvider(expectedEventTypeProviderList)
                .version("1.0.1")
                .build()
                .createObject();
        List<EventTypeProvider> actualEventTypeProviderList = getActionById(action.getActionId()).getEventTypeProvider();
        assertEquals(expectedEventTypeProviderList, actualEventTypeProviderList);
    }

    @DisplayName("Негативный тест на создание allowed_action c event_type_provider не из списка справочника")
    @TmsLink("1267480")
    @Test
    public void createActionWithNotExistEventProviderTest() {
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(new EventTypeProvider("test", "test"));
        String actionName = "create_action_with_not_exist_event_type_provider_test_api";
        Action.builder()
                .actionName(actionName)
                .eventTypeProvider(expectedEventTypeProviderList)
                .version("1.0.1")
                .build()
                .negativeCreateRequest(500);
    }
}
