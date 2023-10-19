package api.cloud.stateService;

import api.Tests;
import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.stateService.Item;
import models.cloud.stateService.extRelations.ExtRelation;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.orderService.OrderServiceSteps;
import steps.stateService.StateServiceSteps;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static core.helper.DateValidator.currentTimeInFormat;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.stateService.ExtRelationsStep.createExtRelation;
import static steps.stateService.ExtRelationsStep.deleteExtRelation;
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
    @DisplayName("Получение списка items по фильтру action.active = true")
    @TmsLink("1428834")
    public void getItemWithActiveActions() {
        Project project = Project.builder().isForOrders(true)
                .projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
                .build()
                .createObject();
        List<Item> itemList = getProjectItemsList(project.getId()).jsonPath().getList("list", Item.class);
        for (Item a : itemList) {
            Item item = getProjectItemsWithIsActiveActions(project.getId(), a.getItemId());
            List<Action> actions = item.getActions();
            if (!actions.isEmpty()) {
                actions.forEach(x -> assertTrue(x.getActive()));
                break;
            }
        }
    }

    @Test
    @DisplayName("Получение списка связок order_id и item_id отфильтрованного по item_id")
    @TmsLink("1429722")
    public void getItemsIdOrderIdListFilteredByItemId() {
        List<String> itemIdList = getItemIdOrderIdList().jsonPath().getList("list.item_id");
        List<Item> itemsIdOrderIdList = getItemIdOrderIdListByItemsIds(itemIdList.get(0), itemIdList.get(1))
                .assertStatus(200)
                .jsonPath()
                .getList("list", Item.class);
        assertEquals(2, itemsIdOrderIdList.size());
        assertTrue(itemIdList.contains(itemIdList.get(0)));
        assertTrue(itemIdList.contains(itemIdList.get(1)));
    }

    @Test
    @DisplayName("Получение списка items with actions по контексту и фильтру")
    @TmsLink("")
    public void getItemListWithActions() {
        Project project = Project.builder().isForOrders(true)
                .build()
                .createObject();
        List<Item> itemsTypeList = getItemsWithActionsByFilter(project.getId(), "type", "vm");
        for (Item item : itemsTypeList) {
            assertEquals("vm", item.getType());
        }
        List<Item> itemsProviderList = getItemsWithActionsByFilter(project.getId(), "provider", "vsphere");
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
        Project project = Project.builder().isForOrders(true).build().createObject();
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
            ZonedDateTime createdRowDt = ZonedDateTime.parse(item.getCreatedRowDt() + "Z");
            assertTrue((createdRowDt.isAfter(startDt) || createdRowDt.isEqual(startDt)) && createdRowDt.isBefore(endDt),
                    String.format("Дата %s itema не входит в заданный промежуток.", createdRowDt));
        }
    }

    @Test
    @DisplayName("Получение списка item со всеми child")
    @TmsLink("1614846")
    public void getItemLisWithAllChildrenTest() {
        Graph graph = createGraph(RandomStringUtils.randomAlphabetic(6).toLowerCase());
        Action action = createAction(RandomStringUtils.randomAlphabetic(6).toLowerCase());
        String orderId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        JSONObject eventJson = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId)
                .type("paas")
                .subtype("build")
                .build()
                .toJson();
        createEventStateService(eventJson);
        String itemId3 = UUID.randomUUID().toString();
        JSONObject json1 = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId3)
                .type("paas")
                .subtype("parent")
                .status(itemId)
                .build()
                .toJson();
        createEventStateService(json1);
        String itemId2 = UUID.randomUUID().toString();
        JSONObject childJson = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId2)
                .type("paas")
                .subtype("parent")
                .status(itemId3)
                .build()
                .toJson();
        createEventStateService(childJson);
        String parent1 = getItemsListWithAllChild("item_id", itemId).jsonPath().getString("list[0].children_list[0].data.parent");
        String parent2 = getItemsListWithAllChild("item_id", itemId).jsonPath().getString("list[0].children_list[0].children_list[0].data.parent");
        assertEquals(parent1, itemId);
        assertEquals(parent2, itemId3);
    }

    @Test
    @DisplayName("Получение списка Items с параметром with_ext_relation=true")
    @TmsLink("1600823")
    public void getItemsListWithExtRelationTrueTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        Item primaryItem = createItem(project);
        Item secondaryItem = createItem(project);
        Item primaryItem2 = createItem(project);
        ExtRelation extRelation = createExtRelation("projects", project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        ExtRelation extRelation2 = createExtRelation("projects", project.getId(), primaryItem2.getItemId(), primaryItem.getItemId(),
                false);
        String newFolder = String.format("/organization/vtb/folder/folder/fold-test/project/%s/", project.getId());
        JSONObject newAction = JsonHelper.getJsonTemplate("stateService/createAction.json")
                .set("$.order_ids", Collections.singletonList(primaryItem.getOrderId()))
                .set("$.graph_id", primaryItem.getGraphId())
                .set("$.action_id", primaryItem.getActions())
                .set("$.data.folder", newFolder)
                .set("$.create_dt", currentTimeInFormat())
                .build();
        createBulkAddAction(project.getId(), newAction);
        Response response = getItemsListByFilter(project.getId(), String.format("order_id=%s&with_ext_relations=true", primaryItem.getOrderId()));
        assertEquals(secondaryItem.getItemId(), response.jsonPath().getString("list[0].data.ext_relations.secondary[0]"));
        assertEquals(primaryItem2.getItemId(), response.jsonPath().getString("list[0].data.ext_relations.primary[0]"));
        deleteExtRelation("projects", project.getId(), extRelation.getId());
        deleteExtRelation("projects", project.getId(), extRelation2.getId());
    }

}
