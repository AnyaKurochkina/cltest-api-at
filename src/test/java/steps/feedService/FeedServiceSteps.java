package steps.feedService;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.feedService.event.Event;
import models.cloud.feedService.event.GetEventList;
import models.cloud.feedService.eventType.EventType;
import models.cloud.feedService.eventType.GetEventTypeList;
import models.cloud.feedService.tag.FeedTag;
import models.cloud.feedService.tag.GetTagsList;
import models.cloud.feedService.targetService.GetTargetServiceList;
import models.cloud.feedService.targetService.TargetService;
import org.json.JSONObject;

import java.util.List;

import static core.helper.Configure.FeedServiceURL;

public class FeedServiceSteps {
    private final static String EVENT_TYPE = "/api/v1/events-feed/event-types/";
    private final static String TARGET_SERVICES = "/api/v1/events-feed/target-services/";
    private final static String EVENTS = "/api/v1/events-feed/events/";
    private final static String TAGS = "/api/v1/events-feed/tags/";

    @Step("Создание Event type под ролью Наблюдатель")
    public static EventType createEventTypeByIdViewer(JSONObject jsonObject) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(jsonObject)
                .post(EVENT_TYPE)
                .assertStatus(201)
                .extractAs(EventType.class);
    }

    @Step("Получение Event type по id")
    public static EventType getEventTypeById(Integer id) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Получение Event type по id под ролью Наблюдатель")
    public static EventType getEventTypeByIdViewer(Integer id) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Получение Target Service по id")
    public static TargetService getTargetServiceById(Integer id) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(TARGET_SERVICES + "{}/", id)
                .assertStatus(200)
                .extractAs(TargetService.class);
    }

    @Step("Получение Event по id")
    public static Event getEventById(Integer id) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(EVENTS + "{}/", id)
                .assertStatus(200)
                .extractAs(Event.class);
    }

    @Step("Получение Tag по id")
    public static FeedTag getTagById(Integer id) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(TAGS + "{}/", id)
                .assertStatus(200)
                .extractAs(FeedTag.class);
    }

    @Step("Получение списка Event type")
    public static GetEventTypeList getEventTypeList() {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(EVENT_TYPE)
                .assertStatus(200)
                .extractAs(GetEventTypeList.class);
    }

    @Step("Получение списка Target Service")
    public static GetTargetServiceList getTargetServiceList() {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(TARGET_SERVICES)
                .assertStatus(200)
                .extractAs(GetTargetServiceList.class);
    }

    @Step("Получение списка Tag")
    public static GetTagsList getTagList() {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(TAGS)
                .assertStatus(200)
                .extractAs(GetTagsList.class);
    }

    @Step("Получение списка Event")
    public static GetEventList getEventList() {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(EVENTS)
                .assertStatus(200)
                .extractAs(GetEventList.class);
    }

    @Step("Обновление Event type")
    public static EventType updateEventType(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Обновление Target Service")
    public static TargetService updateTargetService(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(TARGET_SERVICES + "{}/", id)
                .assertStatus(200)
                .extractAs(TargetService.class);
    }

    @Step("Обновление Tag")
    public static FeedTag updateTag(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(TAGS + "{}/", id)
                .assertStatus(200)
                .extractAs(FeedTag.class);
    }

    @Step("Обновление Event")
    public static Event updateEvent(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(EVENTS + "{}/", id)
                .assertStatus(200)
                .extractAs(Event.class);
    }

    @Step("Частичное обновление Event type")
    public static EventType partialUpdateEventType(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .patch(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Частичное обновление Target Service")
    public static TargetService partialUpdateTargetService(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .patch(TARGET_SERVICES + "{}/", id)
                .assertStatus(200)
                .extractAs(TargetService.class);
    }

    @Step("Частичное обновление Tag")
    public static FeedTag partialUpdateTag(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .patch(TAGS + "{}/", id)
                .assertStatus(200)
                .extractAs(FeedTag.class);
    }

    @Step("Частичное обновление Event")
    public static Event partialUpdateEvent(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .patch(EVENTS + "{}/", id)
                .assertStatus(200)
                .extractAs(Event.class);
    }

    @Step("Удаление Event type")
    public static void deleteEventType(Integer id) {
        new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(EVENT_TYPE + "{}/", id)
                .assertStatus(204);
    }

    @Step("Удаление Event type")
    public static Response deleteForbiddenForDeleteEventType(Integer id) {
        return new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(EVENT_TYPE + "{}/", id)
                .assertStatus(403);
    }

    @Step("Удаление Target Service")
    public static void deleteTargetService(Integer id) {
        new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(TARGET_SERVICES + "{}/", id)
                .assertStatus(204);
    }

    @Step("Удаление Tag")
    public static void deleteTag(Integer id) {
        new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(TAGS + "{}/", id)
                .assertStatus(204);
    }

    @Step("Удаление Event")
    public static void deleteEvent(Integer id) {
        new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(EVENTS + "{}/", id)
                .assertStatus(204);
    }

    @Step("Получение EventType по имени")
    public static EventType getEventTypeByName(String title) {
        List<EventType> eventTypeList = getEventTypeList().getList();
        for (EventType eventType : eventTypeList) {
            if (eventType.getTitle().equals(title)) {
                return eventType;
            }
        }
        return null;
    }

    @Step("Получение TargetService по имени")
    public static TargetService getTargetServiceByName(String title) {
        List<TargetService> eventTypeList = getTargetServiceList().getList();
        for (TargetService targetService : eventTypeList) {
            if (targetService.getTitle().equals(title)) {
                return targetService;
            }
        }
        return null;
    }

    @Step("Получение Tag по имени")
    public static FeedTag getFeedTagByName(String title) {
        List<FeedTag> feedTagList = getTagList().getList();
        for (FeedTag feedTag : feedTagList) {
            if (feedTag.getTitle().equals(title)) {
                return feedTag;
            }
        }
        return null;
    }

    @Step("Получение Tag по имени")
    public static FeedTag getFeedTagByKey(String key) {
        List<FeedTag> feedTagList = getTagList().getList();
        for (FeedTag feedTag : feedTagList) {
            if (feedTag.getKey().equals(key)) {
                return feedTag;
            }
        }
        return null;
    }

    @Step("Проверка существования Tag по ключу")
    public static boolean isFeedTagExist(String key) {
        List<FeedTag> feedTagList = getTagList().getList();
        for (FeedTag feedTag : feedTagList) {
            if (feedTag.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Step("Получение Event по имени")
    public static Event getEventName(String title) {
        List<Event> eventList = getEventList().getList();
        for (Event event : eventList) {
            if (event.getTitle().equals(title)) {
                return event;
            }
        }
        return null;
    }

    @Step("Проверка существования Tag по имени")
    public static boolean isTagExist(String title) {
        List<FeedTag> feedTagList = getTagList().getList();
        for (FeedTag feedTag : feedTagList) {
            if (feedTag.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка существования EventType по имени")
    public static boolean isEventTypeExist(String title) {
        List<EventType> eventTypeList = getEventTypeList().getList();
        for (EventType eventType : eventTypeList) {
            if (eventType.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка существования TargetService по имени")
    public static boolean isTargetServiceExist(String title) {
        List<TargetService> TargetServiceList = getTargetServiceList().getList();
        for (TargetService targetService : TargetServiceList) {
            if (targetService.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }
}
