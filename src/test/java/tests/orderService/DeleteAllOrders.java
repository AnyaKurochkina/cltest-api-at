package tests.orderService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.orderService.OrderServiceSteps;
import tests.Tests;

@DisplayName("Тестовый набор по удалению всех заказов из проекта")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@Order(1)
@Tag("deleteorders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteAllOrders implements Tests {
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @Test
    @Tag("deleteall")
    @DisplayName("Удаление всех успешных заказов из проекта")
    public void DeleteOrders()  {
        orderServiceSteps.deleteOrders("DEV");
    }
}
