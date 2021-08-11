package tests.orderService;

import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

@DisplayName("Тесты на сущности перед заказом продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.ResourcesForOrderTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class ResourcesForOrderTests implements Tests {
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @ParameterizedTest
    @Source(ProductArgumentsProvider.ENV)
    @DisplayName("Получение ресурсных пулов контейнеров")
    public void getResourcesPool(String env) {
        orderServiceSteps.getResourcesPool("container", env);
    }


}
