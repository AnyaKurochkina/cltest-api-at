package models.cloud.productCatalog.allowedAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ActionSteps.deleteActionByName;
import static steps.productCatalog.ActionSteps.isActionExists;
import static steps.productCatalog.AllowedActionSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AllowedAction extends Entity {
    private String description;
    @JsonProperty("item_restriction")
    private Object itemRestriction;
    private String title;
    @JsonProperty("event_type_provider")
    private List<EventTypeProvider> eventTypeProvider;
    @JsonProperty("update_dt")
    private String updateDt;
    private String name;
    @JsonProperty("context_restrictions")
    private List<Object> contextRestriction;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("action")
    private String actionId;
    @JsonProperty("id")
    private Integer id;


    @Override
    public Entity init() {
        if (actionId == null) {
            String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_allowed_action_api_test";
            deleteActionIfExist(actionName);
            Action action = Action.builder().actionName(RandomStringUtils.randomAlphabetic(10).toLowerCase())
                    .build()
                    .createObject();
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
                .set("$.action", actionId)
                .set("$.item_restriction", itemRestriction)
                .set("$.event_type_provider", eventTypeProvider)
                .set("$.context_restrictions", contextRestriction)
                .build();
    }

    @Override
    protected void create() {
        deleteAllowedActionIfExist();
        AllowedAction createAllowedAction = createAllowedAction(toJson())
                .assertStatus(201)
                .compareWithJsonSchema("jsonSchema/allowedAction/postAllowedAction.json")
                .extractAs(AllowedAction.class);
        StringUtils.copyAvailableFields(createAllowedAction, this);
        assertNotNull(actionId, "Действие с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteAllowedActionById(id);
        assertFalse(isAllowedActionExists(name));
    }

    private void deleteActionIfExist(String actionName) {
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
    }

    private void deleteAllowedActionIfExist() {
        if (isAllowedActionExists(name)) {
            deleteAllowedActionByName(name);
        }
    }
}
