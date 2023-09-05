package api.cloud.feedService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.event.Event;
import models.cloud.feedService.eventType.EventType;
import models.cloud.feedService.tag.FeedTag;
import models.cloud.feedService.targetService.TargetService;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import api.Tests;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.feedService.FeedServiceSteps.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("feed_service")
@Epic("Сервис новостей")
@Feature("События")
@DisabledIfEnv("prod")
public class EventTest extends Tests {
    EventType eventType;
    TargetService targetService;
    FeedTag tag;

    @DisplayName("Создание тестовых данных")
    @Tag("health_check")
    @BeforeAll
    public void createTestData() {
        eventType = EventType.builder()
                .internalName("event_type_for_events_test_api")
                .title("event_type_for_events_test_api")
                .build()
                .createObject();
        targetService = TargetService.builder()
                .internalName("target_service_for_events_test_api")
                .title("target_service_for_events_test_api")
                .build()
                .createObject();
        tag = FeedTag.builder()
                .parent(null)
                .key("tag_for_events_test_api")
                .title("tag_for_events_test_api")
                .build()
                .createObject();
    }

    @DisplayName("Создание события")
    @Tag("health_check")
    @TmsLink("977377")
    @Test
    public void createEventTest() {
        Event event = Event.builder()
                .title("create_event_title_test_api")
                .eventType(eventType.getId())
                .content("content test")
                .description("description test")
                .targetService(Collections.singletonList(targetService.getId()))
                .tag(Collections.singletonList(tag.getId()))
                .build()
                .createObject();
        Event createdEvent = getEventById(event.getId());
        assertEquals(event.getTitle(), createdEvent.getTitle());
        assertEquals(event.getEventType(), createdEvent.getEventType());
        assertEquals(event.getTag(), createdEvent.getTag());
        assertEquals(event.getTargetService(), createdEvent.getTargetService());
    }

    @DisplayName("Получение события по id")
    @TmsLink("977378")
    @Test
    public void getEventTest() {
        Event event = Event.builder()
                .title("get_by_id_event_title_test_api")
                .eventType(eventType.getId())
                .content("content test")
                .description("description test")
                .targetService(Collections.singletonList(targetService.getId()))
                .tag(Collections.singletonList(tag.getId()))
                .build()
                .createObject();
        Event createdEvent = getEventById(event.getId());
        assertEquals(event.getTitle(), createdEvent.getTitle());
        assertEquals(event.getEventType(), createdEvent.getEventType());
        assertEquals(event.getTag(), createdEvent.getTag());
        assertEquals(event.getTargetService(), createdEvent.getTargetService());
    }

    @DisplayName("Получение списка событий")
    @TmsLink("977434")
    @Test
    public void getEventListTest() {
        Event event = Event.builder()
                .title("get_list_by_id_event_title_test_api")
                .eventType(eventType.getId())
                .content("content test")
                .description("description test")
                .targetService(Collections.singletonList(targetService.getId()))
                .tag(Collections.singletonList(tag.getId()))
                .build()
                .createObject();
        Event createdEvent = getEventById(event.getId());
        List<Event> list = getEventList().getList();
        assertTrue(list.contains(createdEvent));
    }

    @DisplayName("Обновление события")
    @Tag("health_check")
    @TmsLink("977438")
    @Test
    public void updateEventTest() {
        Event event = Event.builder()
                .title("update_event_title_test_api")
                .eventType(eventType.getId())
                .content("content test")
                .description("description test")
                .targetService(Collections.singletonList(targetService.getId()))
                .tag(Collections.singletonList(tag.getId()))
                .build()
                .createObject();
        Event expectedEvent = Event.builder()
                .title("updated_event_title_test_api")
                .eventType(eventType.getId())
                .content("updated test")
                .description("updated description test")
                .targetService(Collections.singletonList(targetService.getId()))
                .tag(Collections.singletonList(tag.getId()))
                .build()
                .createObject();
        JSONObject body = expectedEvent.init().toJson();
        Event actualEvent = updateEvent(event.getId(), body);
        assertEquals(expectedEvent.getTitle(), actualEvent.getTitle());
        assertEquals(expectedEvent.getDescription(), actualEvent.getDescription());
    }

    @DisplayName("Частичное обновление события")
    @TmsLink("977441")
    @Test
    public void partialUpdateEventTest() {
        Event event = Event.builder()
                .title("partial_update_event_title_test_api")
                .eventType(eventType.getId())
                .content("content test")
                .description("description test")
                .targetService(Collections.singletonList(targetService.getId()))
                .tag(Collections.singletonList(tag.getId()))
                .build()
                .createObject();
        String expectedDescription = "partial_update";
        JSONObject body = new JSONObject().put("description", expectedDescription);
        Event actualEvent = partialUpdateEvent(event.getId(), body);
        assertEquals(expectedDescription, actualEvent.getDescription());
    }

    @DisplayName("Удаление события")
    @Tag("health_check")
    @TmsLink("977445")
    @Test
    public void deleteEventTest() {
        Event event = Event.builder()
                .title("delete_event_title_test_api")
                .eventType(eventType.getId())
                .content("content test")
                .description("description test")
                .targetService(Collections.singletonList(targetService.getId()))
                .tag(Collections.singletonList(tag.getId()))
                .build()
                .createObject();
        Event createdEvent = getEventById(event.getId());
        deleteEvent(event.getId());
        List<Event> list = getEventList().getList();
        assertFalse(list.contains(createdEvent));
    }
}
