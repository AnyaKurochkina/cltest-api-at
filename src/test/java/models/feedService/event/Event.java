package models.feedService.event;

import core.helper.JsonHelper;
import core.helper.http.Http;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.feedService.eventType.EventType;
import org.json.JSONObject;

import java.util.List;

import static core.helper.Configure.FeedServiceURL;

@Log4j2
@Getter
@EqualsAndHashCode(exclude = {"jsonTemplate", "feedService"})
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"jsonTemplate", "feedService"})
public class Event extends Entity {
    private Integer id;
    private List<Integer> targetService;
    private String externalLink;
    private String endDate;
    private EventType eventTypeInfo;
    private Boolean show;
    private String description;
    private Integer eventType;
    private String title;
    private String content;
    private String startDate;
    @Getter(AccessLevel.NONE)
    private final String feedService = "/api/v1/events-feed/events/";
    @Getter(AccessLevel.NONE)
    private String jsonTemplate;

    @Builder
    public Event(List<Integer> targetService, String externalLink, String endDate, EventType eventTypeInfo,
                 Boolean show, String description, Integer eventType, String title, String content, String startDate) {
        this.targetService = targetService;
        this.externalLink = externalLink;
        this.endDate = endDate;
        this.eventTypeInfo = eventTypeInfo;
        this.show = show;
        this.description = description;
        this.eventType = eventType;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
    }

    @Override
    public Entity init() {
        jsonTemplate = "feedService/createEvent.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.targetService", targetService)
                .set("$.externalLink", externalLink)
                .set("$.endDate", endDate)
                .set("$.eventTypeInfo", eventTypeInfo)
                .set("$.show", show)
                .set("$.description", description)
                .set("$.eventType", eventType)
                .set("$.title", title)
                .set("$.content", content)
                .set("$.startDate", startDate)
                .build();
    }

    @Override
    protected void create() {
        id = new Http(FeedServiceURL)
                .body(toJson())
                .post(feedService)
                .assertStatus(201)
                .extractAs(Event.class)
                .getId();
    }

    @Override
    protected void delete() {
        new Http(FeedServiceURL)
                .delete(feedService + id + "/")
                .assertStatus(204);
    }
}