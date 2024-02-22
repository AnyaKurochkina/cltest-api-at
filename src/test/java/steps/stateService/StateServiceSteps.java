package steps.stateService;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.stateService.*;
import org.json.JSONObject;
import steps.Steps;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

import static core.helper.Configure.stateServiceURL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;

@Log4j2
public class StateServiceSteps extends Steps {

    @Step("Создание BulkAddAction")
    public static Response createBulkAddAction(String projectId, JSONObject json) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .body(json)
                .post("/api/v1/projects/{}/actions/bulk-add-action/", projectId);
    }

    @Step("Создание BulkAddEvent")
    public static Response createBulkAddEvent(String projectId, JSONObject json) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .body(json)
                .post("/api/v1/projects/{}/events/bulk-add-event/", projectId);
    }

    @Step("Создание Item")
    public static Item createItem(Project project) {
        String uuid = UUID.randomUUID().toString();
        Action action = createAction(StringUtils.getRandomStringApi(7));
        Graph graph = createGraph(StringUtils.getRandomStringApi(7));
        JSONObject json2 = JsonHelper.getJsonTemplate("stateService/createBulkAddEvent.json")
                .set("$.order_id", uuid)
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getId())
                .set("$.events[0].item_id", uuid)
                .set("$.events[0].graph_id", graph.getGraphId())
                .set("$.events[0].action_id", action.getId())
                .set("$.events[1].item_id", uuid)
                .set("$.events[1].graph_id", graph.getGraphId())
                .set("$.events[1].action_id", action.getId())
                .set("$.events[2].item_id", uuid)
                .set("$.events[2].graph_id", graph.getGraphId())
                .set("$.events[2].action_id", action.getId())
                .build();
        createBulkAddEvent(project.getId(), json2);
        return getItemById(uuid);
    }

    public static String getErrorFromStateService(String projectId, String orderId) {
        String text = null;
        try {
            text = new Http(stateServiceURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/api/v1/projects/{}/actions/?order_id={}&status=error", projectId, orderId)
                    .assertStatus(200)
                    .jsonPath().getString("list.findAll{it.status.contains('error')}.data.traceback");
        } catch (Throwable e) {
            log.error("Ошибка при получении ошибки заказа", e);
        }
        if (text != null)
            Allure.getLifecycle().addAttachment("StateService", "text/html", "log", text.getBytes(StandardCharsets.UTF_8));
        return text;
    }

    @Step("Получение списка id из списка items")
    public static List<String> getOrdersIdList(String projectId) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/", projectId)
                .assertStatus(200)
                .jsonPath()
                .getList("list.order_id");
    }

    @Step("Получение списка items по контексту projects")
    public static Response getProjectItemsList(String projectId) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/", projectId)
                .assertStatus(200);
    }

    @Step("Получение списка item_id и order_id")
    public static Response getItemIdOrderIdList() {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/order_id/")
                .assertStatus(200);
    }

    @Step("Получение списка item_id и order_id по фильтру item_id")
    public static Response getItemIdOrderIdListByItemsIds(String... itemIds) {
        String ids = String.join(",", itemIds);
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/order_id/?item_id__in=" + ids);
    }

    @Step("Получение списка items c actions(active=true) по контексту projects")
    public static Item getProjectItemsWithIsActiveActions(String projectId, String itemId) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/?item_id={}&active=true&with_actions=true", projectId, itemId)
                .assertStatus(200)
                .jsonPath()
                .getObject("list[0]", Item.class);
    }

    @Step("Получение списка items у которых parent {isExist}")
    public static List<Item> getItemsWithParentExist(boolean isExist) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/?parent_exists={}", isExist)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items with actions по контексту и по фильтру {filter}")
    public static List<Item> getItemsWithActionsByFilter(String projectId, String filter, String value) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/projects/{}/items/?with_actions=true&{}={}", projectId, filter, value)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items with folder и по фильтру {filter}")
    public static List<Item> getItemsWithActionsByFilter(String filter, String value) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/?{}={}&with_folder=true", filter, value)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items по значению ключа {key} в data.config")
    public static List<Item> getItemsByDataConfigKey(String key, String value) {
        List<Item> result = new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/?data__config__{}={}", key, value)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
        assertFalse(result.isEmpty(), "Ключа или ключа с таким значением не существует");
        return result;
    }

    @Step("Получение версии state service")
    public static Response getStateServiceVersion() {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/version/")
                .assertStatus(200);
    }

    @Step("Создаем Event")
    public static Response createEventStateService(JSONObject body) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .body(body)
                .post("/api/v1/events/")
                .assertStatus(201);
    }

    @Step("Получение статуса health")
    public static String getHealthStateService() {
        return new Http(stateServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/health/")
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    @Step("Получение списка items с параметром with_parent_item=true")
    public static List<Item> getItemsWithParentItem() {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/?with_parent_item=true")
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение item по id = {itemId} с параметром with_parent_item=true")
    public static List<Item> getItemByItemIdWithParentItem(String itemId) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/{}/?with_parent_item=true", itemId)
                .assertStatus(200)
                .jsonPath()
                .getList("", Item.class);
    }

    @Step("Получение списка items")
    public static List<Item> getItemsList() {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/")
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка primary items, в которых искомый item_id значится как secondary")
    public static List<Item> getPrimaryItemsList(String id) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/as_secondary_items/?item_id={}", id)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка secondary items, в которых искомый item_id значится как primary")
    public static List<Item> getSecondaryItemsList(String id) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/as_primary_items/?item_id={}", id)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items у которых нет primary связи с конкретным item_id")
    public static List<Item> getItemsListWithOutPrimaryRelation(String id) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/?ext_rel_primary_not={}", id)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items у которых нет secondary связи с конкретным item_id")
    public static List<Item> getItemsListWithOutSecondaryRelation(String id) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/?ext_rel_secondary_not={}", id)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items по фильтру {filter}")
    public static List<Item> getItemsListByFilter(String filter) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/?{}", filter)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items по фильтру {filter}")
    public static Response getResponseByFilter(String filter) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/?{}", filter);

    }

    @Step("Получение списка items со всеми child")
    public static Response getItemsListWithAllChild(String filter, String value) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/items/all_children_list/?{}={}", filter, value)
                .assertStatus(200);

    }

    @Step("Получение списка items по фильтру {filter}")
    public static Response getItemsListByFilter(String contextId, String filter) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/?{}", contextId, filter)
                .assertStatus(200);
    }

    @Step("Получение item по id={id} и фильтру {filter}")
    public static Item getItemByIdAndFilter(String id, String filter) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/{}/?{}", id, filter)
                .assertStatus(200)
                .extractAs(Item.class);
    }

    @Step("Получение item по id={id}")
    public static Item getItemById(String id) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/{}/", id)
                .assertStatus(200)
                .extractAs(Item.class);
    }

    @Step("Получение статистики по Items и контексту")
    public static Response getItemStatByProjectId(String projectId) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/stats/", projectId)
                .assertStatus(200);
    }

    @Step("Отправка на тарификацию всех items ордера")
    public static Response uncheckedOrderItemsPublication(String projectId, String orderId) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .body(new JSONObject().put("order_id", orderId))
                .post("/api/v1/projects/{}/items/order_items_publication/", projectId);
    }

    @Step("Получение последней ошибки в проекте по контексту")
    public static String getLastErrorByProjectId(String projectId) {
        return new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/actions/?status=error", projectId)
                .assertStatus(200)
                .jsonPath()
                .getString("list[0].data.traceback");
    }

    public static List<ShortItem> getItems(String id) {
        List<Item> list = new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/?page=1&per_page=10000&data__state__in=off,on", id)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
        List<ShortItem> listOrders = new ArrayList<>();
        list.forEach(item -> {
            ShortItem itemData = new ShortItem();

            if (item.getType().equals("public_ip"))
                itemData.floatingIpAddress = ((Map<String, String>) item.getData().get("config")).get("floating_ip_address");
            else if (item.getType().equals("volume")) {
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
                itemData.size = ((Map<String, Number>) item.getData().get("config")).get("size").longValue();
            } else if (item.getType().equals("snapshot")) {
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
                itemData.size = ((Map<String, Number>) item.getData().get("config")).get("size").longValue();
            } else if (item.getType().equals("image")) {
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
                itemData.size = ((Map<String, Number>) item.getData().get("config")).get("size").longValue();
            } else if (item.getType().equals("instance"))
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
            else if (item.getType().equals("nic")) {
                List<Object> ips = ((Map<String, List<Object>>) item.getData().get("config")).get("fixed_ips");
                itemData.name = ((Map<String, String>) ips.get(0)).get("ip_address");
            } else if (item.getType().equals("vip")) {
                List<Object> ips = ((Map<String, List<Object>>) item.getData().get("config")).get("fixed_ips");
                itemData.floatingIpAddress = ((Map<String, String>) ips.get(0)).get("ip_address");
            }

            itemData.type = item.getType();
            itemData.provider = (String) item.getData().get("provider");
            itemData.srcOrderId = (String) item.getData().getOrDefault("src_order_id", "");
            itemData.parent = (String) item.getData().getOrDefault("parent", "");
            itemData.itemId = item.getItemId();

            itemData.orderId = item.getOrderId();
//            List<ShortItem> shortItemList = listOrders.getOrDefault(item.getOrderId(), new ArrayList<>());
//            shortItemList.add(itemData);
            listOrders.add(itemData);
        });
        log.debug("getItems: {}", JsonHelper.toJson(listOrders));
        return listOrders;
    }


    public static List<ActionStateService> getActionListByFilter(String filter, String value) {
        return new Http(stateServiceURL)
                .withServiceToken()
                .get("/api/v1/actions/?{}={}&per_page=500", filter, value)
                .assertStatus(200)
                .extractAs(GetActionStateServiceList.class)
                .getList();
    }

    public static List<EventStateService> getEventListByFilter(String filter, String value) {
        return new Http(stateServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/events/?{}={}", filter, value)
                .assertStatus(200)
                .extractAs(GetEventStateServiceList.class)
                .getList();
    }

    @Step("Получение длительности узлов по actionId {actionId}")
    public static void getNodesDuration(String[] actionId) {
        HashMap<String, ArrayList<Integer>> map = new HashMap<>();
        for (String act : actionId) {
            List<ActionStateService> actionsList = getActionListByFilter("action_id", act);
            for (ActionStateService action : actionsList) {
                if (action.getStatus().contains("completed") && action.getSubtype().equals("run_node")) {
                    ActionStateService actionStarted = actionsList.stream()
                            .filter(a -> a.getStatus().equals(action.getStatus().replace("completed", "started")))
                            .findFirst().get();
                    Integer millis = Math.toIntExact(Duration.
                            between(ZonedDateTime.parse(actionStarted.getCreateDt()), ZonedDateTime.parse(action.getCreateDt())).toMillis());
                    if (!map.containsKey(action.getStatus().split(":")[0])) {
                        ArrayList<Integer> list = new ArrayList<>();
                        list.add(millis);
                        map.put(action.getStatus().split(":")[0], list);
                    } else {
                        map.get(action.getStatus().split(":")[0]).add(millis);
                    }
                }
            }
        }
        ArrayList<String> outList = new ArrayList();
        map.forEach((k, v) -> {
            outList.add(k + "," + v.stream().mapToInt(Integer::intValue).min().orElse(0) + "," + v.stream().mapToInt(Integer::intValue).average().orElse(0) + "," + v.stream().mapToInt(Integer::intValue).max().orElse(0));
        });
        Collections.sort(outList);
        for (String k : outList) {
            System.out.println(k + "\r");
        }

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ShortItem {
        private String orderId;
        private String name;
        private String parent;
        private String provider;
        private Long size;
        private String srcOrderId;
        private String type;
        private String floatingIpAddress;
        private String itemId;
    }
}
