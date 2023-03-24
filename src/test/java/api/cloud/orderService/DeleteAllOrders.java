package api.cloud.orderService;

import lombok.AllArgsConstructor;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import api.Tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DisplayName("Тестовый набор по удалению всех заказов из проекта")
@Execution(ExecutionMode.CONCURRENT)
@Order(1)
@Tag("deleteorders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteAllOrders extends Tests {

    @ParameterizedTest(name = "{0}")
    @Tag("deleteAll")
    @Source(ProductArgumentsProvider.ENV)
    @DisplayName("Удаление всех успешных заказов из проекта")
    public void DeleteOrders(String env)  {
        OrderServiceSteps.deleteOrders(env);
    }

    @Test
    @DisplayName("Вывод всех ошибочных заказов")
    void printAllErrorOrders()  {
        List<Order> orders = new ArrayList<>();
        List<String> projects = Arrays.asList("proj-ln4zg69jek", "proj-rddf0uwi0q", "proj-ahjjqmlgnm",
                "proj-bhbyhmik3a", "proj-zoz17np8rb", "proj-114wetem0c", "proj-1oob0zjo5h", "proj-6wpfrbes0g", "proj-aei4kz2yu4",
                "proj-lcwn3pwg7z", "proj-50duh5yxy6", "proj-xryy5l8ei5", "proj-yhi3rxo07h");
        for (String projectId : projects) {
            OrderServiceSteps.getProductsWithStatus(projectId, "changing", "damaged", "failure", "pending", "locked")
                    .forEach(e -> orders.add(new Order(e, projectId)));
        }
        for (Order order : orders) {
            System.out.printf("https://prod-portal-front.cloud.vtb.ru/all/orders/%s/main?context=%s&type=project&org=vtb%n",
                    order.id, order.projectId);
        }
    }

    @AllArgsConstructor
    private static class Order {
        String id;
        String projectId;
    }
}
