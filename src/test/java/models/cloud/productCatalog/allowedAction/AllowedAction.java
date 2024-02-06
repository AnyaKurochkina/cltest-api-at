package models.cloud.productCatalog.allowedAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import java.util.List;

import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.AllowedActionSteps.deleteAllowedActionById;
import static tests.routes.AllowedActionProductCatalogApi.apiV1AllowedActionsCreate;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"updateDt"})
@ToString
public class AllowedAction extends AbstractEntity {
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
    private String object_info;
    @JsonProperty("id")
    private Integer id;

    public JSONObject toJson() {
        if (actionId == null) {
            String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_allowed_action_api_test";
            deleteActionIfExist(actionName);
            Action action = createAction(actionName);
            actionId = action.getId();
        }
        return JsonHelper.getJsonTemplate("productCatalog/allowedAction/createAllowedAction.json")
                .set("$.title", title)
                .set("$.description", description)
                .set("$.action", actionId)
                .set("$.item_restriction", itemRestriction)
                .set("$.event_type_provider", eventTypeProvider)
                .set("$.context_restrictions", contextRestriction)
                .set("$.object_info", object_info)
                .build();
    }

    public AllowedAction createObject() {
        return getProductCatalogAdmin()
                .body(this.toJson())
                .api(apiV1AllowedActionsCreate)
                .extractAs(AllowedAction.class)
                .deleteMode(Mode.AFTER_TEST);
    }

    @Override
    public void delete() {
        deleteAllowedActionById(id);
    }

    private void deleteActionIfExist(String actionName) {
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
    }
}
