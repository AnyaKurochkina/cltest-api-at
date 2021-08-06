package tests.orderService;

import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

import java.util.stream.Stream;

@DisplayName("Тесты на сущности перед заказом продуктов")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.orderService.ResourcesForOrderTest")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prod")})
public class ResourcesForOrderTest extends Tests {
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @ParameterizedTest
    @MethodSource("dataProviderMethod")
    @DisplayName("Получение ресурсных пулов контейнеров")
    public void getResourcesPool(String env) {
        orderServiceSteps.getResourcesPool("container", env);
    }

    static Stream<Arguments> dataProviderMethod() {
        return Stream.of(
                Arguments.arguments("DEV"),
                Arguments.arguments("TEST"));
    }

}
