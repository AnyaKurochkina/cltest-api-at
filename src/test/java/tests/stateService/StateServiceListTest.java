package tests.stateService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.orderService.OrderServiceSteps;
import steps.stateService.StateServiceSteps;
import tests.Tests;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
@Tag("state_service")
@Epic("State Service")
@Feature("Items")
@DisabledIfEnv("prod")
public class StateServiceListTest extends Tests {

    @Test
    @DisplayName("Получение списка items")
    @TmsLink("1069385")
    public void getItemList() {
        Project project = Project.builder().isForOrders(true)
                .projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
                .build()
                .createObject();
        List<String> ordersId = OrderServiceSteps.getProductsWithAllStatus(project.getId());
        List<String> ordersIdItems = StateServiceSteps.getOrdersIdList(project.getId());
        List<String> ids = ordersIdItems.stream().distinct().collect(Collectors.toList());
        assertTrue(ordersId.containsAll(ids));
    }
}
