package models.feedService.targetService;

import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import static core.helper.Configure.FeedServiceURL;

@Log4j2
@Getter
@EqualsAndHashCode(exclude = {"jsonTemplate", "feedService"})
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"jsonTemplate", "feedService"})
public class TargetService extends Entity {
    private Integer id;
    private String title;
    private String internalName;
    @Getter(AccessLevel.NONE)
    private String jsonTemplate;
    @Getter(AccessLevel.NONE)
    private String feedService;

    @Builder
    public TargetService(String title, String internalName) {
        this.title = title;
        this.internalName = internalName;
    }

    @Override
    public Entity init() {
        jsonTemplate = "feedService/createTargetService.json";
        feedService = "/api/v1/events-feed/target-services/";
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
    @Step("Создание TargetService")
    protected void create() {
        id = new Http(FeedServiceURL)
                .body(toJson())
                .post(feedService)
                .assertStatus(201)
                .extractAs(TargetService.class)
                .getId();
    }

    @Override
    @Step("Удаление TargetService")
    protected void delete() {
        new Http(FeedServiceURL)
                .delete(feedService + id + "/")
                .assertStatus(204);
    }
}
