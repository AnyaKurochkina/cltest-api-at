package models.cloud.productCatalog.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.VersionDiff;
import models.cloud.productCatalog.graph.Graph;
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
    @JsonProperty("icon_base64")
    private String iconBase64;
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
    @JsonProperty("restricted_groups")
    private List<String> restrictedGroups;
    @JsonProperty("graph_id")
    private String graphId;
    private String version;
    @JsonProperty("last_version")
    private String lastVersion;
    @JsonProperty("data_config_key")
    private String dataConfigKey;
    @JsonProperty("name")
    private String name;
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
    private String versionCreateDt;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    @JsonProperty("multiple")
    private boolean isMultiple;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("extra_data")
    private Map<String, String> extraData;
    @JsonProperty("allowed_developers")
    private List<String> allowedDevelopers;
    @JsonProperty("restricted_developers")
    private List<String> restrictedDevelopers;
    @JsonProperty("version_diff")
    private VersionDiff versionDiff;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("event_type_provider")
    private List<EventTypeProvider> eventTypeProvider;
    @JsonProperty("context_restrictions")
    private Object contextRestrictions;
    @JsonProperty("is_safe")
    private Boolean isSafe;
    @JsonProperty("is_delayable")
    private Boolean isDelayable;
    @JsonProperty("version_fields")
    private List<String> versionFields;
    @JsonProperty("is_delayable")
    private Boolean isDelayable;

    @Override
    public Entity init() {
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_action_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/actions/createAction.json")
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.type", type)
                .set("$.current_version", currentVersion)
                .set("$.description", description)
                .set("$.graph_id", graphId)
                .set("$.graph_version", graphVersion)
                .set("$.version", version)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .setIfNullRemove("$.is_delayable", isDelayable)
                .set("$.priority", priority)
                .set("$.extra_data", extraData)
                .set("$.restricted_groups", restrictedGroups)
                .set("$.allowed_groups", allowedGroups)
                .set("$.location_restriction", locationRestriction)
                .set("$.context_restrictions", contextRestrictions)
                .set("$.event_type_provider", eventTypeProvider)
                .setIfNullRemove("$.is_safe", isSafe)
                .setIfNullRemove("$.number", number)
                .build();
    }

    @Override
    protected void create() {
        if (isActionExists(name)) {
            deleteActionByName(name);
        }
        Action createAction = createAction(toJson())
                .assertStatus(201)
                .extractAs(Action.class);
        StringUtils.copyAvailableFields(createAction, this);
        assertNotNull(actionId, "Действие с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteActionById(actionId);
        assertFalse(isActionExists(name));
    }
}
