package steps.feedService;

import core.helper.http.Http;
import io.qameta.allure.Step;
import models.feedService.event.Event;
import models.feedService.eventType.EventType;
import models.feedService.eventType.GetEventTypeList;
import models.feedService.targetService.GetTargetServiceList;
import models.feedService.targetService.TargetService;
import org.json.JSONObject;

import static core.helper.Configure.FeedServiceURL;

public class FeedServiceSteps {
    private final static String EVENT_TYPE = "/api/v1/events-feed/event-types/";
    private final static String TARGET_SERVICES = "/api/v1/events-feed/target-services/";
    private final static String EVENTS = "/api/v1/events-feed/events/";

    @Step("Получение Event type по id")
    public static EventType getEventTypeById(Integer id) {
        return new Http(FeedServiceURL)
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

    @Step("Удаление Event type")
    public static void deleteEventType(Integer id) {
        new Http(FeedServiceURL)
                .delete(EVENT_TYPE + "{}/", id)
                .assertStatus(204);
    }

    @Step("Удаление Target Service")
    public static void deleteTargetService(Integer id) {
        new Http(FeedServiceURL)
                .delete(TARGET_SERVICES + "{}/", id)
                .assertStatus(204);
    }
}
