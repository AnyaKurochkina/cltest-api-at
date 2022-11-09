package steps.stateService;

import core.enums.Role;
import core.helper.Configure;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.cloud.stateService.GetItemList;
import models.cloud.stateService.Item;
import org.json.JSONObject;
import ru.testit.annotations.LinkType;
import ru.testit.junit5.StepsAspects;
import ru.testit.services.LinkItem;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.StateServiceURL;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Log4j2
public class StateServiceSteps extends Steps {

    public static String getErrorFromStateService(String orderId) {
        String traceback = null;
        try {
            traceback = new Http(StateServiceURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/actions/?order_id={}", orderId)
                    .jsonPath().getString("list.findAll{it.status.contains('error')}.data.traceback");
        } catch (JsonPathException e) {
            log.error(e.toString());
        }
        if (StepsAspects.getCurrentStep().get() != null) {
            StepsAspects.getCurrentStep().get().addLinkItem(
                    new LinkItem("State service log", String.format("%s/actions/?order_id=%s", Configure.getAppProp("url.stateService"), orderId), "", LinkType.REPOSITORY));
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
}
