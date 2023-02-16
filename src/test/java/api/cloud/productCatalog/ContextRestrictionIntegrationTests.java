package api.cloud.productCatalog;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.Astra;
import models.cloud.productCatalog.action.Action;
import models.cloud.feedService.action.EventTypeProvider;
import org.junit.DisabledIfEnv;
import org.junit.EnabledIfEnv;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import api.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("product_catalog")
@Epic("Интеграционные тесты. Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ContextRestrictionIntegrationTests extends Tests {
    @TmsLink("")
    @EnabledIfEnv("ift")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание действия в продуктовом каталоге с ограничениями по context_restriction")
    @Disabled
    public void checkActionWithContextRestriction(Astra product) {
        String actionName = "action_for_context_restriction_api_test";
        Action action = Action.builder()
                .name(actionName )
                .title(actionName )
                .eventTypeProvider(Collections.singletonList(new EventTypeProvider("vm", "vsphere")))
                .requiredItemStatuses(Collections.singletonList("on"))
                .requiredOrderStatuses(Collections.singletonList("success"))
                .build()
                .createObject();
        OrderServiceSteps.registrationAction(action.getName());
        try (Astra astra = product.createObjectExclusiveAccess()) {
            assertTrue((Boolean) OrderServiceSteps.getProductsField(astra,String.format("data[0].actions.any{it.name==%s}", actionName)));
            int i = 0;
        }
    }
}
