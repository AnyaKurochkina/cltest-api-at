package tests.orderService;

import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

@DisplayName("Тестовый набор по удалению всех заказов из проекта")
@Execution(ExecutionMode.CONCURRENT)
@Order(1)
@Tag("deleteorders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteAllOrders extends Tests {
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @ParameterizedTest(name = "{0}")
    @Tag("deleteAll")
    @Source(ProductArgumentsProvider.ENV)
    @DisplayName("Удаление всех успешных заказов из проекта")
    public void DeleteOrders(String env, String tmsId)  {
        orderServiceSteps.deleteOrders(env);
    }
}
