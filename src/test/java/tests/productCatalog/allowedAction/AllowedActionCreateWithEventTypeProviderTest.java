package tests.productCatalog.allowedAction;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.action.EventTypeProvider;
import models.productCatalog.allowedAction.AllowedAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String actionName = "create_allowed_action_with_event_provider_test_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
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
        List<EventTypeProvider> expectedEventTypeProviderList =
                Collections.singletonList(new EventTypeProvider("test", "test"));
        String actionName = "create_action_test_api";
        AllowedAction.builder()
                .name(actionName)
                .eventTypeProvider(expectedEventTypeProviderList)
                .build()
                .negativeCreateRequest(500);
    }
}
