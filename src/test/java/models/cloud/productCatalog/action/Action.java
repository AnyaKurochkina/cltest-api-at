package models.cloud.productCatalog.action;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.VersionDiff;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static steps.productCatalog.ActionSteps.deleteActionById;
import static steps.productCatalog.GraphSteps.createGraph;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"active", "id", "createDt", "updateDt", "versionCreateDt"}, callSuper = false)
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Action extends AbstractEntity {

    @Builder.Default
    private Boolean availableWithoutMoney = false;
    private List<String> versionList;
    private String currentVersion;
    private Integer priority;
    @JsonAlias({"icon_store_id", "icon_store"})
    private String iconStoreId;
    @Builder.Default
    private String iconUrl = "";
    private String iconBase64;
    @Builder.Default
    private String locationRestriction = "";
    private String graphVersion;
    private String description;
    private Boolean skipOnPrebilling;
    private Object itemRestriction;
    private Boolean autoRemovingIfFailed;
    private String title;
    private String type;
    private List<String> requiredItemStatuses;
    private String dataConfigPath;
    private List<Object> restrictedPaths;
    private String graphVersionPattern;
    private String id;
    private List<Object> allowedPaths;
    private List<String> restrictedGroups;
    private String graphId;
    private String version;
    private String object_info;
    private String lastVersion;
    private String dataConfigKey;
    private String name;
    private String inactiveReason;
    private Integer number;
    private List<Object> allowedGroups;
    private String graphVersionCalculated;
    private List<Object> dataConfigFields;
    private List<Object> requiredOrderStatuses;
    private String versionCreateDt;
    private String versionChangedByUser;
    private boolean multiple;
    private String createDt;
    private String updateDt;
    private Map<String, String> extraData;
    private List<String> allowedDevelopers;
    private List<String> restrictedDevelopers;
    private VersionDiff versionDiff;
    private Boolean active;
    private List<EventTypeProvider> eventTypeProvider;
    private Object contextRestrictions;
    @Builder.Default
    private Boolean isSafe = true;
    @Builder.Default
    private Boolean isDelayable = false;
    private Boolean availableWithCostReduction;
    private List<String> versionFields;
    private List<String> tagList;
    private Boolean skipRequestResourcePools;
    private Boolean skipReservation;
    private Boolean skipValidateChecker;
    private Boolean ignoreRestrictionService;
    private Boolean skipRestrictionService;
    private Boolean skipItemChange;
    private Boolean skipItemWithSecondaryRel;
    private Boolean isForItems;
    private Boolean isOnlyForApi;

    public JSONObject toJson() {
        if (graphId == null) {
            Graph graph = createGraph(StringUtils.getRandomStringApi(6));
            graphId = graph.getGraphId();
            graphVersionCalculated = graph.getVersion();
        }
        return JsonHelper.getJsonTemplate("productCatalog/actions/createAction.json")
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .set("$.name", name)
                .set("$.object_info", object_info)
                .set("$.title", title)
                .set("$.type", type)
                .set("$.current_version", currentVersion)
                .set("$.description", description)
                .set("$.graph_id", graphId)
                .set("$.graph_version", graphVersion)
                .set("$.graph_version_calculated", graphVersionCalculated)
                .set("$.graph_version_pattern", graphVersionPattern)
                .set("$.version", version)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .setIfNullRemove("$.is_delayable", isDelayable)
                .set("$.priority", priority)
                .set("$.extra_data", extraData)
                .set("$.restricted_groups", restrictedGroups)
                .set("$.allowed_groups", allowedGroups)
                .setIfNullRemove("$.location_restriction", locationRestriction)
                .set("$.context_restrictions", contextRestrictions)
                .set("$.event_type_provider", eventTypeProvider)
                .set("$.tag_list", tagList)
                .set("$.required_item_statuses", requiredItemStatuses)
                .setIfNullRemove("$.available_with_cost_reduction", availableWithCostReduction)
                .setIfNullRemove("$.is_safe", isSafe)
                .setIfNullRemove("$.number", number)
                .setIfNullRemove("$.skip_request_resource_pools", skipRequestResourcePools)
                .setIfNullRemove("$.skip_validate_checker", skipValidateChecker)
                .setIfNullRemove("$.ignore_restriction_service", ignoreRestrictionService)
                .setIfNullRemove("$.skip_restriction_service", skipRestrictionService)
                .setIfNullRemove("$.available_without_money", availableWithoutMoney)
                .setIfNullRemove("$.skip_reservation", skipReservation)
                .setIfNullRemove("$.skip_item_change", skipItemChange)
                .setIfNullRemove("$.skip_on_prebilling", skipOnPrebilling)
                .setIfNullRemove("$.skip_item_with_secondary_rel", skipItemWithSecondaryRel)
                .setIfNullRemove("$.is_for_items", isForItems)
                .setIfNullRemove("$.is_only_for_api", isOnlyForApi)
                .build();
    }

    public Integer getPrioritise() {
        return priority;
    }

    @Override
    public void delete() {
        deleteActionById(id);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
