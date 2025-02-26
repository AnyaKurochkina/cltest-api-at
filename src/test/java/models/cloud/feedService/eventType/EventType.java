package models.cloud.feedService.eventType;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.feedService.event.Event;
import org.json.JSONObject;
import steps.feedService.FeedServiceSteps;

import java.util.List;
import java.util.Objects;

import static core.helper.Configure.feedServiceURL;
import static steps.feedService.FeedServiceSteps.deleteEvent;
import static steps.feedService.FeedServiceSteps.getEventList;

@Log4j2
@Getter
@EqualsAndHashCode(exclude = {"jsonTemplate", "feedService"})
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"jsonTemplate", "feedService"})
public class EventType extends Entity {
    private Integer id;
    private String title;
    private String internalName;
    @Getter(AccessLevel.NONE)
    private String jsonTemplate;
    @Getter(AccessLevel.NONE)
    private String feedService;

    @Builder
    public EventType(String title, String internalName) {
        this.title = title;
        this.internalName = internalName;
    }

    @Override
    public Entity init() {
        feedService = "/api/v1/events-feed/event-types/";
        jsonTemplate = "feedService/createEventType.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.title", title)
                .set("$.internalName", internalName)
                .build();
    }

    @Override
    @Step("Создание EventType")
    protected void create() {
        EventType eventTypeByName = FeedServiceSteps.getEventTypeByName(title);
        if (eventTypeByName != null) {
            List<Event> eventList = getEventList().getList();
            for (Event event : eventList) {
                if (!Objects.isNull(event.getEventTypeInfo())) {
                    if (title.equals(event.getEventTypeInfo().getTitle())) {
                        deleteEvent(event.getId());
                    }
                }
            }
            FeedServiceSteps.deleteEventType(eventTypeByName.getId());
        }
        id = new Http(feedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(feedService)
                .assertStatus(201)
                .extractAs(EventType.class)
                .getId();
    }

    @Override
    @Step("Удаление EventType")
    protected void delete() {
        new Http(feedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(feedService + id + "/")
                .assertStatus(204);
    }
}
