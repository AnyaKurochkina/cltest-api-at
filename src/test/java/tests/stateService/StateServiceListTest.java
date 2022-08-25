package tests.stateService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import models.stateService.Item;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.orderService.OrderServiceSteps;
import steps.stateService.StateServiceSteps;
import tests.Tests;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static steps.stateService.StateServiceSteps.getItemsByDataConfigKey;
import static steps.stateService.StateServiceSteps.getItemsWithParentExist;

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

    @Test
    @DisplayName("Получение списка items у которых parent=true")
    @TmsLink("1126714")
    public void getItemListWithParentIsExistTrue() {
        List<Item> list = getItemsWithParentExist(true);
        for (Item item : list) {
            assertNotNull(item.getData().get("parent"));
        }
    }

    @Test
    @DisplayName("Получение списка items у которых parent=false")
    @TmsLink("1126716")
    public void getItemListWithParentIsExistFalse() {
        List<Item> list = getItemsWithParentExist(false);
        for (Item item : list) {
            assertNull(item.getData().get("parent"));
        }
    }

    @Test
    @DisplayName("Получение списка items по значению ключа в data.config")
    @TmsLink("")
    public void getItemListWithByDataConfigKey() {
        String key = "environment";
        String value = "IFT";
        List<Item> list = getItemsByDataConfigKey(key, value);
        for (Item item : list) {
            Object config = item.getData().get("config");
            LinkedHashMap<String, Object> configMap = (LinkedHashMap) config;
            assertEquals(value, configMap.get(key));
        }
    }
}
