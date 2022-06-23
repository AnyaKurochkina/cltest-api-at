package tests.feedService;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.feedService.eventType.EventType;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.feedService.FeedServiceSteps.*;

@Tag("feed_service")
@Epic("Сервис новостей")
@Feature("Тип события")
@DisabledIfEnv("prod")
public class EventTypeTest extends Tests {

    @DisplayName("Создание типа событий")
    @TmsLink("953459")
    @Test
    public void createEventType() {
        EventType eventType = EventType.builder()
                .title("create_event_type_title_test_api")
                .internalName("create_event_type_internalName_test_api")
                .build()
                .createObject();
        EventType createdEventType = getEventTypeById(eventType.getId());
        assertEquals(eventType, createdEventType);
    }

    @DisplayName("Создание типа событий под ролью Наблюдатель")
    @TmsLink("")
    @Test
    public void createEventTypeByViewerTest() {
        EventType eventType = EventType.builder()
                .title("create_event_type_title_test_api")
                .internalName("create_event_type_internalName_test_api")
                .build();
        JSONObject body = eventType.init().toJson();
        createEventTypeByIdViewer(body);
    }

    @DisplayName("Получение типа событий по id")
    @TmsLink("953933")
    @Test
    public void getEventTypeTest() {
        EventType eventType = EventType.builder()
                .title("get_event_type_title_test_api")
                .internalName("get_event_type_internalName_test_api")
                .build()
                .createObject();
        EventType createdEventType = getEventTypeById(eventType.getId());
        assertEquals(eventType, createdEventType);
    }

    @DisplayName("Получение типа событий по id под ролью Наблюдатель")
    @TmsLink("")
    @Test
    public void getEventTypeByViewerTest() {
        EventType eventType = EventType.builder()
                .title("get_event_type_title_test_api")
                .internalName("get_event_type_internalName_test_api")
                .build()
                .createObject();
        EventType createdEventType = getEventTypeByIdViewer(eventType.getId());
        assertEquals(eventType, createdEventType);
    }

    @DisplayName("Получение списка типа событий")
    @TmsLink("953463")
    @Test
    public void getEventTypeListTest() {
        EventType eventType = EventType.builder()
                .title("get_list_event_type_title_test_api")
                .internalName("get_list_create_event_type_internalName_test_api")
                .build()
                .createObject();
        List<EventType> list = getEventTypeList().getList();
        assertTrue(list.contains(eventType));
    }

    @DisplayName("Обновление типа событий")
    @TmsLink("953465")
    @Test
    public void updateEventTypeTest() {
        EventType eventType = EventType.builder()
                .title("update_event_type_title_test_api")
                .internalName("update_create_event_type_internalName_test_api")
                .build()
                .createObject();
        EventType expectedEventType = EventType.builder()
                .title("updated_event_type_title_test_api")
                .internalName("updated_event_type_internalName_test_api")
                .build();
        JSONObject body = expectedEventType.init().toJson();
        EventType actualEventType = updateEventType(eventType.getId(), body);
        assertEquals(expectedEventType.getTitle(), actualEventType.getTitle());
        assertEquals(expectedEventType.getInternalName(), actualEventType.getInternalName());
    }

    @DisplayName("Частичное обновление типа событий")
    @TmsLink("953467")
    @Test
    public void partialUpdateEventTypeTest() {
        EventType eventType = EventType.builder()
                .title("partial_update_event_type_title_test_api")
                .internalName("partial_update_event_type_internalName_test_api")
                .build()
                .createObject();
        String expectedInternalName = "partial_update";
        JSONObject body = new JSONObject().put("internalName", expectedInternalName);
        EventType actualEventType = partialUpdateEventType(eventType.getId(), body);
        assertEquals(expectedInternalName, actualEventType.getInternalName());
    }

    @DisplayName("Удаление типа событий")
    @TmsLink("953495")
    @Test
    public void deleteEventTypeTest() {
        EventType eventType = EventType.builder()
                .title("delete_event_type_title_test_api")
                .internalName("delete_event_type_internalName_test_api")
                .build()
                .createObject();
        deleteEventType(eventType.getId());
        List<EventType> list = getEventTypeList().getList();
        assertFalse(list.contains(eventType));
    }

    @DisplayName("Удаление типа событий Сервис")
    @TmsLink("977567")
    @Test
    public void deleteServiceEventTypeTest() {
        EventType eventType = getEventTypeByName("Сервис");
        Response response = deleteForbiddenForDeleteEventType(eventType.getId());
        assertEquals("У вас недостаточно прав для выполнения данного действия.", response.jsonPath().get("detail"));
        List<EventType> list = getEventTypeList().getList();
        assertTrue(list.contains(eventType));
    }

    @DisplayName("Удаление типа событий Информация")
    @TmsLink("977568")
    @Test
    public void deleteInfoEventTypeTest() {
        EventType eventType = getEventTypeByName("Информация");
        Response response = deleteForbiddenForDeleteEventType(eventType.getId());
        assertEquals("У вас недостаточно прав для выполнения данного действия.", response.jsonPath().get("detail"));
        List<EventType> list = getEventTypeList().getList();
        assertTrue(list.contains(eventType));
    }
}
