package models.productCatalog.allowedAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.productCatalog.action.Action;
import org.json.JSONObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.AllowedActionSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
    @JsonProperty("context_restrictions")
    private List<String> contextRestriction;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("action")
    private String actionId;
    @JsonProperty("id")
    private Integer id;


    @Override
    public Entity init() {
        if (actionId == null) {
            Action action = Action.builder().actionName("action_for_allowed_action_api_test").build().createObject();
            actionId = action.getActionId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/allowedAction/createAllowedAction.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.event_provider", eventProvider)
                .set("$.action", actionId)
                .set("$.item_restriction", itemRestriction)
                .set("$.event_type", eventType)
                .set("$.config_restriction", configRestriction)
                .set("$.environment_type_restriction", environmentTypeRestriction)
                .set("$.context_restrictions", contextRestriction)
                .build();
    }

    @Override
    protected void create() {
        if (isAllowedActionExists(name)) {
            deleteAllowedActionById(id);
        }
        AllowedAction createAllowedAction = createAllowedAction(toJson())
                .assertStatus(201)
                .extractAs(AllowedAction.class);
        StringUtils.copyAvailableFields(createAllowedAction, this);
        assertNotNull(actionId, "Действие с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteAllowedActionById(id);
        assertFalse(isAllowedActionExists(name));
    }
}
