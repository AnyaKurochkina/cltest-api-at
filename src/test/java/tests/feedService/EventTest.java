package tests.feedService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.feedService.event.Event;
import models.feedService.eventType.EventType;
import models.feedService.targetService.TargetService;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import tests.Tests;

import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("feed_service")
@Epic("Сервис новостей")
@Feature("События")
@DisabledIfEnv("prod")
public class EventTest extends Tests {
    EventType eventType;
    TargetService targetService;

    @DisplayName("Создание тестовых данных")
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
    }

    @DisplayName("Создание события")
    @TmsLink("")
    @Test
    public void createEventTest() {
        Event event = Event.builder()
                .title("create_event_title_test_api")
                .eventType(eventType.getId())
                .content("content test")
                .description("description test")
                .targetService(Arrays.asList(targetService.getId()))
                .build()
                .createObject();
    }
}
