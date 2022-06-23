package steps.feedService;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.feedService.event.Event;
import models.feedService.event.GetEventList;
import models.feedService.eventType.EventType;
import models.feedService.eventType.GetEventTypeList;
import models.feedService.tag.FeedTag;
import models.feedService.tag.GetTagsList;
import models.feedService.targetService.GetTargetServiceList;
import models.feedService.targetService.TargetService;
import org.json.JSONObject;

import java.util.List;

import static core.helper.Configure.FeedServiceURL;

public class FeedServiceSteps {
    private final static String EVENT_TYPE = "/api/v1/events-feed/event-types/";
    private final static String TARGET_SERVICES = "/api/v1/events-feed/target-services/";
    private final static String EVENTS = "/api/v1/events-feed/events/";
    private final static String TAGS= "/api/v1/events-feed/tags/";

    @Step("Создание Event type под ролью Наблюдатель")
    public static EventType createEventTypeByIdViewer(JSONObject jsonObject) {
        return new Http(FeedServiceURL)
                .setRole(Role.VIEWER)
                .body(jsonObject)
                .post(EVENT_TYPE)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Получение Event type по id")
    public static EventType getEventTypeById(Integer id) {
        return new Http(FeedServiceURL)
                .get(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Получение Event type по id под ролью Наблюдатель")
    public static EventType getEventTypeByIdViewer(Integer id) {
        return new Http(FeedServiceURL)
                .setRole(Role.VIEWER)
                .get(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Получение Target Service по id")
    public static TargetService getTargetServiceById(Integer id) {
        return new Http(FeedServiceURL)
                .get(TARGET_SERVICES + "{}/", id)
                .assertStatus(200)
                .extractAs(TargetService.class);
    }

    @Step("Получение Event по id")
    public static Event getEventById(Integer id) {
        return new Http(FeedServiceURL)
                .get(EVENTS + "{}/", id)
                .assertStatus(200)
                .extractAs(Event.class);
    }

    @Step("Получение Tag по id")
    public static FeedTag getTagById(Integer id) {
        return new Http(FeedServiceURL)
                .get(TAGS + "{}/", id)
                .assertStatus(200)
                .extractAs(FeedTag.class);
    }

    @Step("Получение списка Event type")
    public static GetEventTypeList getEventTypeList() {
        return new Http(FeedServiceURL)
                .get(EVENT_TYPE)
                .assertStatus(200)
                .extractAs(GetEventTypeList.class);
    }

    @Step("Получение списка Target Service")
    public static GetTargetServiceList getTargetServiceList() {
        return new Http(FeedServiceURL)
                .get(TARGET_SERVICES)
                .assertStatus(200)
                .extractAs(GetTargetServiceList.class);
    }

    @Step("Получение списка Tag")
    public static GetTagsList getTagList() {
        return new Http(FeedServiceURL)
                .get(TAGS)
                .assertStatus(200)
                .extractAs(GetTagsList.class);
    }

    @Step("Получение списка Event")
    public static GetEventList getEventList() {
        return new Http(FeedServiceURL)
                .get(EVENTS)
                .assertStatus(200)
                .extractAs(GetEventList.class);
    }

    @Step("Обновление Event type")
    public static EventType updateEventType(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .put(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Обновление Target Service")
    public static TargetService updateTargetService(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .put(TARGET_SERVICES + "{}/", id)
                .assertStatus(200)
                .extractAs(TargetService.class);
    }

    @Step("Обновление Tag")
    public static FeedTag updateTag(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .put(TAGS + "{}/", id)
                .assertStatus(200)
                .extractAs(FeedTag.class);
    }

    @Step("Обновление Event")
    public static Event updateEvent(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .put(EVENTS + "{}/", id)
                .assertStatus(200)
                .extractAs(Event.class);
    }

    @Step("Частичное обновление Event type")
    public static EventType partialUpdateEventType(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .patch(EVENT_TYPE + "{}/", id)
                .assertStatus(200)
                .extractAs(EventType.class);
    }

    @Step("Частичное обновление Target Service")
    public static TargetService partialUpdateTargetService(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .patch(TARGET_SERVICES + "{}/", id)
                .assertStatus(200)
                .extractAs(TargetService.class);
    }

    @Step("Частичное обновление Tag")
    public static FeedTag partialUpdateTag(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .patch(TAGS + "{}/", id)
                .assertStatus(200)
                .extractAs(FeedTag.class);
    }

    @Step("Частичное обновление Event")
    public static Event partialUpdateEvent(Integer id, JSONObject body) {
        return new Http(FeedServiceURL)
                .body(body)
                .patch(EVENTS + "{}/", id)
                .assertStatus(200)
                .extractAs(Event.class);
    }

    @Step("Удаление Event type")
    public static void deleteEventType(Integer id) {
         new Http(FeedServiceURL)
                .delete(EVENT_TYPE + "{}/", id)
                .assertStatus(204);
    }

    @Step("Удаление Event type")
    public static Response deleteForbiddenForDeleteEventType(Integer id) {
        return new Http(FeedServiceURL)
                .delete(EVENT_TYPE + "{}/", id)
                .assertStatus(403);
    }

    @Step("Удаление Target Service")
    public static void deleteTargetService(Integer id) {
        new Http(FeedServiceURL)
                .delete(TARGET_SERVICES + "{}/", id)
                .assertStatus(204);
    }

    @Step("Удаление Tag")
    public static void deleteTag(Integer id) {
        new Http(FeedServiceURL)
                .delete(TAGS + "{}/", id)
                .assertStatus(204);
    }

    @Step("Удаление Event")
    public static void deleteEvent(Integer id) {
        new Http(FeedServiceURL)
                .delete(EVENTS + "{}/", id)
                .assertStatus(204);
    }

    @Step("Получение EventType по имени")
    public static EventType getEventTypeByName(String title) {
        List<EventType> eventTypeList = getEventTypeList().getList();
        for(EventType eventType : eventTypeList) {
            if (eventType.getTitle().equals(title)) {
                return eventType;
            }
        }
        return null;
    }
}
