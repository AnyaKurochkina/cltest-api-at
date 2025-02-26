package models.cloud.feedService.targetService;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import steps.feedService.FeedServiceSteps;

import static core.helper.Configure.feedServiceURL;

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
        TargetService targetServiceByName = FeedServiceSteps.getTargetServiceByName(title);
        if (targetServiceByName != null) {
            FeedServiceSteps.deleteTargetService(targetServiceByName.getId());
        }
        id = new Http(feedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(feedService)
                .assertStatus(201)
                .extractAs(TargetService.class)
                .getId();
    }

    @Override
    @Step("Удаление TargetService")
    protected void delete() {
        new Http(feedServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(feedService + id + "/")
                .assertStatus(204);
    }
}
