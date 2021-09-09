package tests.orderService;

import io.qameta.allure.Allure;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

@DisplayName("Получение ресурсных пулов контейнеров")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.ResourcesForOrderTests")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod"), @Tag("smoke")})
public class ResourcesForOrderTests implements Tests {
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @ParameterizedTest(name = "{0}")
    @Source(ProductArgumentsProvider.ENV)
    @DisplayName("Получение ресурсных пулов контейнеров")
    public void getResourcesPool(String env, String tmsId) {
        Allure.tms("17." + tmsId, "");
        orderServiceSteps.getResourcesPool("container", env);
    }


}
