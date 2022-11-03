package models.cloud.feedService.event;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.feedService.eventType.EventType;
import models.cloud.feedService.tag.FeedTag;
import models.cloud.feedService.targetService.TargetService;
import org.json.JSONObject;
import steps.feedService.FeedServiceSteps;

import java.util.List;

import static core.helper.Configure.FeedServiceURL;

@Log4j2
@Getter
@EqualsAndHashCode(exclude = {"jsonTemplate", "feedService"})
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"jsonTemplate", "feedService"})
public class Event extends Entity {

    private List<TargetService> targetServiceInfo;
    private String image;
    private String externalLink;
    private String endDate;
    private EventType eventTypeInfo;
    private Boolean show;
    private String description;
    private Integer eventType;
    private String title;
    private String content;
    private List<FeedTag> tagInfo;
    private String createdAt;
    private List<Integer> targetService;
    private Integer id;
    private List<Integer> tag;
    private String startDate;
    private String updatedAt;
    @Getter(AccessLevel.NONE)
    private String feedService;
    @Getter(AccessLevel.NONE)
    private String jsonTemplate;

    @Builder
    public Event(List<TargetService> targetServiceInfo, String image, String externalLink, String endDate,
                 EventType eventTypeInfo, Boolean show, String description, Integer eventType, String title,
                 String content, List<FeedTag> tagInfo, String createdAt, List<Integer> targetService,
                 List<Integer> tag, String startDate, String updatedAt, String feedService) {
        this.targetServiceInfo = targetServiceInfo;
        this.image = image;
        this.externalLink = externalLink;
        this.endDate = endDate;
        this.eventTypeInfo = eventTypeInfo;
        this.show = show;
        this.description = description;
        this.eventType = eventType;
        this.title = title;
        this.content = content;
        this.tagInfo = tagInfo;
        this.createdAt = createdAt;
        this.targetService = targetService;
        this.tag = tag;
        this.startDate = startDate;
        this.updatedAt = updatedAt;
        this.feedService = feedService;
    }

    @Override
    public Entity init() {
        feedService = "/api/v1/events-feed/events/";
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
                .set("$.tag", tag)
                .build();
    }

    @Override
    protected void create() {
        Event event = FeedServiceSteps.getEventName(title);
        if (event != null) {
            FeedServiceSteps.deleteEvent(event.getId());
        }
        id = new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(feedService)
                .assertStatus(201)
                .extractAs(Event.class)
                .getId();
    }

    @Override
    protected void delete() {
        new Http(FeedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(feedService + id + "/")
                .assertStatus(204);
    }
}