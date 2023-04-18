package steps.stateService;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.path.json.exception.JsonPathException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import models.cloud.stateService.*;
import org.json.JSONObject;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import steps.Steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static core.helper.Configure.StateServiceURL;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Log4j2
public class StateServiceSteps extends Steps {

    @Step("Создание BulkAddAction")
    public static Response createBulkAddAction(String projectId, JSONObject json) {
        return new Http(StateServiceURL)
                .withServiceToken()
                .body(json)
                .post("/api/v1/projects/{}/actions/bulk-add-action/", projectId);
    }

    @Step("Создание BulkAddEvent")
    public static Response createBulkAddEvent(String projectId, JSONObject json) {
        return new Http(StateServiceURL)
                .withServiceToken()
                .body(json)
                .post("/api/v1/projects/{}/events/bulk-add-event/", projectId);
    }

    public static String getErrorFromStateService(String orderId) {
        String traceback = null;
        try {
            traceback = new Http(StateServiceURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/api/v1/actions/?order_id={}", orderId)
                    .jsonPath().getString("list.findAll{it.status.contains('error')}.data.traceback");
        } catch (JsonPathException e) {
            log.error(e.toString());
        }
        if (StepsAspects.getCurrentStep().get() != null) {
            StepsAspects.getCurrentStep().get().addLinkItem(
                    new LinkItem("State service log", String.format("%s/api/v1/actions/?order_id=%s", Configure.getAppProp("url.stateService"), orderId), "", LinkType.REPOSITORY));
        }
        return traceback;
    }

    @Step("Получение списка id из списка items")
    public static List<String> getOrdersIdList(String projectId) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/", projectId)
                .assertStatus(200)
                .jsonPath()
                .getList("list.order_id");
    }

    @Step("Получение списка items по контексту projects")
    public static Response getProjectItemsList(String projectId) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/", projectId)
                .assertStatus(200);
    }

    @Step("Получение списка item_id и order_id")
    public static Response getItemIdOrderIdList() {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/order_id/")
                .assertStatus(200);
    }

    @Step("Получение списка item_id и order_id по фильтру item_id")
    public static Response getItemIdOrderIdListByItemsIds(String... itemIds) {
        String ids = String.join(",", itemIds);
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/order_id/?item_id__in=" + ids);
    }

    @Step("Получение списка items c actions(active=true) по контексту projects")
    public static Item getProjectItemsWithIsActiveActions(String projectId, String itemId) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/?item_id={}&active=true&with_actions=true", projectId, itemId)
                .assertStatus(200)
                .jsonPath()
                .getObject("list[0]", Item.class);
    }

    @Step("Получение списка items у которых parent {isExist}")
    public static List<Item> getItemsWithParentExist(boolean isExist) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/?parent_exists={}", isExist)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items with actions по контексту и по фильтру {filter}")
    public static List<Item> getItemsWithActionsByFilter(String projectId, String filter, String value) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/?with_actions=true&{}={}", projectId, filter, value)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items with folder и по фильтру {filter}")
    public static List<Item> getItemsWithActionsByFilter(String filter, String value) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/?{}={}&with_folder=true", filter, value)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items по значению ключа {key} в data.config")
    public static List<Item> getItemsByDataConfigKey(String key, String value) {
        List<Item> result = new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/?data__config__{}={}", key, value)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
        assertFalse(result.isEmpty(), "Ключа или ключа с таким значением не существует");
        return result;
    }

    @Step("Получение версии state service")
    public static Response getStateServiceVersion() {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/version/")
                .assertStatus(200);
    }

    @Step("Создаем Event")
    public static Response createEventStateService(JSONObject body) {
        return new Http(StateServiceURL)
                .withServiceToken()
                .body(body)
                .post("/api/v1/events/")
                .assertStatus(201);
    }

    @Step("Получение статуса health")
    public static String getHealthStateService() {
        return new Http(StateServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/health/")
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    @Step("Получение списка items с параметром with_parent_item=true")
    public static List<Item> getItemsWithParentItem() {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/?with_parent_item=true")
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение item по id = {itemId} с параметром with_parent_item=true")
    public static List<Item> getItemByItemIdWithParentItem(String itemId) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/{}/?with_parent_item=true", itemId)
                .assertStatus(200)
                .jsonPath()
                .getList("", Item.class);
    }

    @Step("Получение списка items")
    public static List<Item> getItemsList() {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/")
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение списка items по фильтру {filter}")
    public static List<Item> getItemsListByFilter(String filter) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/?{}", filter)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
    }

    @Step("Получение item по id={id} и фильтру {filter}")
    public static Item getItemByIdAndFilter(String id, String filter) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/items/{}/?{}", id, filter)
                .assertStatus(200)
                .extractAs(Item.class);
    }

    @Step("Получение статистики по Items и контексту")
    public static Response getItemStatByProjectId(String projectId) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/stats/", projectId)
                .assertStatus(200);
    }

    @Step("Получение последней ошибки в проекте по контексту")
    public static String getLastErrorByProjectId(String projectId) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/actions/?status=error", projectId)
                .assertStatus(200)
                .jsonPath()
                .getString("list[0].data.traceback");
    }

    public static List<ShortItem> getItems(String id){
        List<Item> list = new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/items/?page=1&per_page=10000&data__state__in=off,on", id)
                .assertStatus(200)
                .extractAs(GetItemList.class)
                .getList();
//        JSONObject jsonObject = new JSONObject();
        List<ShortItem> listOrders = new ArrayList<>();
        list.forEach(item -> {
            ShortItem itemData = new ShortItem();

            if(item.getType().equals("public_ip"))
                itemData.setFloatingIpAddress(((Map<String, String>) item.getData().get("config")).get("floating_ip_address"));
            else if(item.getType().equals("volume")){
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
                itemData.size = ((Map<String, Number>) item.getData().get("config")).get("size").longValue();
            }
            else if(item.getType().equals("snapshot")){
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
                itemData.size = ((Map<String, Number>) item.getData().get("config")).get("size").longValue();
            }
            else if(item.getType().equals("image")){
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
                itemData.size = ((Map<String, Number>) item.getData().get("config")).get("size").longValue();
            }
            else if(item.getType().equals("instance"))
                itemData.name = ((Map<String, String>) item.getData().get("config")).get("name");
            else if(item.getType().equals("nic")) {
                List<Object> ips = ((Map<String, List<Object>>) item.getData().get("config")).get("fixed_ips");
                itemData.name = ((Map<String, String>) ips.get(0)).get("ip_address");
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
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/actions/?{}={}", filter, value)
                .assertStatus(200)
                .extractAs(GetActionStateServiceList.class)
                .getList();
    }

    public static List<EventStateService> getEventListByFilter(String filter, String value) {
        return new Http(StateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/events/?{}={}", filter, value)
                .assertStatus(200)
                .extractAs(GetEventStateServiceList.class)
                .getList();
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
