package models.productCatalog.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.productCatalog.VersionDiff;
import models.productCatalog.graph.Graph;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ActionSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "active", callSuper = false)
public class Action extends Entity {

    @JsonProperty("available_without_money")
    private Boolean availableWithoutMoney;
    @JsonProperty("version_list")
    private List<String> versionList;
    @JsonProperty("current_version")
    private String currentVersion;
    private Integer priority;
    @JsonProperty("icon_store_id")
    private String iconStoreId;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("location_restriction")
    private String locationRestriction;
    @JsonProperty("graph_version")
    private String graphVersion;
    private String description;
    @JsonProperty("skip_on_prebilling")
    private Boolean skipOnPrebilling;
    @JsonProperty("item_restriction")
    private Object itemRestriction;
    @JsonProperty("auto_removing_if_failed")
    private Boolean autoRemovingIfFailed;
    private String title;
    private String type;
    @JsonProperty("event_type")
    private List<Object> eventType;
    @JsonProperty("required_item_statuses")
    private List<Object> requiredItemStatuses;
    @JsonProperty("data_config_path")
    private String dataConfigPath;
    @JsonProperty("restricted_paths")
    private List<Object> restrictedPaths;
    @JsonProperty("graph_version_pattern")
    private String graphVersionPattern;
    @JsonProperty("id")
    private String actionId;
    @JsonProperty("allowed_paths")
    private List<Object> allowedPaths;
    @JsonProperty("event_provider")
    private List<Object> eventProvider;
    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;
    @JsonProperty("graph_id")
    private String graphId;
    private String version;
    @JsonProperty("last_version")
    private String lastVersion;
    @JsonProperty("config_restriction")
    private String configRestriction;
    @JsonProperty("data_config_key")
    private String dataConfigKey;
    @JsonProperty("name")
    private String actionName;
    private Integer number;
    @JsonProperty("allowed_groups")
    private List<Object> allowedGroups;
    @JsonProperty("graph_version_calculated")
    private String graphVersionCalculated;
    @JsonProperty("data_config_fields")
    private List<Object> dataConfigFields;
    @JsonProperty("required_order_statuses")
    private List<Object> requiredOrderStatuses;
    @JsonProperty("version_create_dt")
    private String version_create_dt;
    @JsonProperty("version_changed_by_user")
    private String version_changed_by_user;
    @JsonProperty("multiple")
    private boolean isMultiple;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("extra_data")
    private Map<String, String> extraData;
    @JsonProperty("allowed_developers")
    private List<String> allowed_developers;
    @JsonProperty("restricted_developers")
    private List<String> restricted_developers;
    @JsonProperty("version_diff")
    private VersionDiff versionDiff;
    @JsonProperty("active")
    private Boolean active;
    private String jsonTemplate;
    @JsonProperty("context_restrictions")
    private Object contextRestrictions;

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/actions/createAction.json";
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_action_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .set("$.name", actionName)
                .set("$.title", title)
                .set("$.type", type)
                .set("$.current_version", currentVersion)
                .set("$.description", description)
                .set("$.graph_id", graphId)
                .set("$.version", version)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .set("$.priority", priority)
                .set("$.extra_data", extraData)
                .set("$.location_restriction", locationRestriction)
                .set("$.context_restrictions", contextRestrictions)
                .setIfNullRemove("$.number", number)
                .build();
    }

    @Override
    protected void create() {
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        Action createAction = createAction(toJson())
                .assertStatus(201)
                .extractAs(Action.class);
        StringUtils.copyAvailableFields(createAction, this);
        assertNotNull(actionId, "Действие с именем: " + actionName + ", не создался");
    }

    @Override
    protected void delete() {
        deleteActionById(actionId);
        assertFalse(isActionExists(actionName));
    }
}
