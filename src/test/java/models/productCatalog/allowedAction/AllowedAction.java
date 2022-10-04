package models.productCatalog.allowedAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.productCatalog.action.Action;
import org.json.JSONObject;

import java.util.List;

import static steps.productCatalog.AllowedActionSteps.isAllowedActionExists;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "active", callSuper = false)
public class AllowedAction extends Entity {
    @JsonProperty("event_provider")
    private List<String> eventProvider;
    private String description;
    @JsonProperty("item_restriction")
    private Object itemRestriction;
    private String title;
    @JsonProperty("event_type")
    private List<String> eventType;
    @JsonProperty("environment_type_restriction")
    private List<String> environmentTypeRestriction;
    @JsonProperty("config_restriction")
    private Object configRestriction;
    @JsonProperty("update_dt")
    private String updateDt;
    private String name;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("action")
    private String actionId;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("is_all_levels")
    private Boolean isAllLevels;
    @JsonProperty("direction")
    private String direction;
    private String jsonTemplate;
    private String productName;
    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/allowedAction/createAllowedAction.json";
        productName = "/api/v1/allowed_actions/";
        if (actionId == null) {
            Action action = Action.builder().actionName("action_for_allowed_action_api_test").build().createObject();
            actionId = action.getActionId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.event_provider", eventProvider)
                .set("$.action", actionId)
                .set("$.item_restriction", itemRestriction)
                .set("$.event_type", eventType)
                .set("$.config_restriction", configRestriction)
                .set("$.is_all_levels", isAllLevels)
                .set("$.direction", direction)
                .set("$.environment_type_restriction", environmentTypeRestriction)
                .build();
    }

    @Override
    protected void create() {
        if (isAllowedActionExists(name)) {

        }

    }

    @Override
    protected void delete() {

    }
}
