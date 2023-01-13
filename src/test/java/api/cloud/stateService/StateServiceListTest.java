package api.cloud.stateService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.stateService.Item;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.orderService.OrderServiceSteps;
import steps.stateService.StateServiceSteps;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static steps.stateService.StateServiceSteps.*;

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
    @DisplayName("Получение списка items with actions по контексту и фильтру")
    @TmsLink("")
    public void getItemListWithActions() {
        // TODO: 26.09.2022 разобраться с проектом.
        Project.builder().isForOrders(true)
                .projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
                .build()
                .createObject();
        List<Item> itemsTypeList = getItemsWithActionsByFilter("proj-42pomp56tw", "type", "vm");
        for (Item item : itemsTypeList) {
            assertEquals("vm", item.getType());
        }
        List<Item> itemsProviderList = getItemsWithActionsByFilter("proj-42pomp56tw", "provider", "vsphere");
        for (Item item : itemsProviderList) {
            assertEquals("vsphere", item.getProvider());
        }
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
    @TmsLink("1129761")
    public void getItemListWithByDataConfigKey() {
        String key = "environment";
        String value = "DEV";
        List<Item> list = getItemsByDataConfigKey(key, value);
        for (Item item : list) {
            Object config = item.getData().get("config");
            LinkedHashMap<String, Object> configMap = (LinkedHashMap) config;
            assertEquals(value, configMap.get(key));
        }
    }

    @Test
    @DisplayName("Получение списка items с параметром with_parent_items=true")
    @TmsLink("1241198")
    public void getItemListWithParentsItemsTrue() {
        List<Item> list = getItemsWithParentItem();
        for (Item item : list) {
            if (item.getData().get("parent") != null) {
                LinkedHashMap<String, Object> parent_item = (LinkedHashMap) item.getData().get("parent_item");
                assertNotNull(parent_item.get("item_id"));
                assertNotNull(parent_item.get("type"));
                assertNotNull(parent_item.get("data"));
            }
        }
    }

    @Test
    @DisplayName("Получение списка items с параметром with_folder=true")
    @TmsLink("1283814")
    public void getItemListWithFolderTrueTest() {
        List<Item> list = getItemsListByFilter("with_folder=true");
        for (Item item : list) {
            assertNotNull(item.getFolder());
        }
    }

    @Test
    @DisplayName("Получение статистики по Items")
    @TmsLink("1350167")
    public void getItemsStatTest() {
        Project project = Project.builder().build().createObject();
        getItemStatByProjectId(project.getId()).compareWithJsonSchema("jsonSchema/ItemsStatSchema.json");
    }

    @Test
    @DisplayName("Получение списка items созданных за промежуток времени")
    @TmsLink("1343112")
    public void getItemListCreatedOverAPeriodOfTimeTest() {
        ZonedDateTime startDt = ZonedDateTime.parse("2022-11-23T10:41:00.000000Z");
        ZonedDateTime endDt = ZonedDateTime.parse("2022-11-23T12:42:00.000000Z");
        List<Item> list = getItemsListByFilter(String.format("created_row_dt__gte=%s&created_row_dt__lt=%s", startDt, endDt));
        for (Item item : list) {
            ZonedDateTime createdRowDt = ZonedDateTime.parse(item.getCreatedRowDt());
            assertTrue((createdRowDt.isAfter(startDt) || createdRowDt.isEqual(startDt)) && createdRowDt.isBefore(endDt),
                    String.format("Дата %s itema не входит в заданный промежуток.", createdRowDt));
        }
    }
}
