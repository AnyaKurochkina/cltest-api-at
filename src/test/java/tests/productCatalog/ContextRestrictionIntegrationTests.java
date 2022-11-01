package tests.productCatalog;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Astra;
import models.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.EnabledIfEnv;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

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
    public void checkActionWithContextRestriction(Astra product) {
        String actionName = "action_for_context_restriction_api_test";
        Action action = Action.builder()
                .actionName(actionName )
                .title(actionName )
                .eventType(Collections.singletonList("vm"))
                .eventProvider(Collections.singletonList("vsphere"))
                .requiredItemStatuses(Collections.singletonList("on"))
                .requiredOrderStatuses(Collections.singletonList("success"))
                .build()
                .createObject();
        OrderServiceSteps.registrationAction(action.getActionName());
        try (Astra astra = product.createObjectExclusiveAccess()) {
            assertTrue((Boolean) OrderServiceSteps.getProductsField(astra,String.format("data[0].actions.any{it.name==%s}", actionName)));
            int i = 0;
        }
    }
}
