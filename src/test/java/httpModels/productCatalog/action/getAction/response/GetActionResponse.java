package httpModels.productCatalog.action.getAction.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.productCatalog.VersionDiff;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetActionResponse implements GetImpl {

    @JsonProperty("available_without_money")
    private Boolean availableWithoutMoney;

    @JsonProperty("version_list")
    private List<String> versionList;

    @JsonProperty("current_version")
    private String currentVersion;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("icon_store_id")
    private String iconStoreId;

    @JsonProperty("icon_url")
    private String iconUrl;

    @JsonProperty("location_restriction")
    private String locationRestriction;

    @JsonProperty("graph_version")
    private String graphVersion;

    @JsonProperty("description")
    private String description;

    @JsonProperty("skip_on_prebilling")
    private Boolean skipOnPrebilling;

    @JsonProperty("item_restriction")
    private Object itemRestriction;

    @JsonProperty("auto_removing_if_failed")
    private Boolean autoRemovingIfFailed;

    @JsonProperty("title")
    private String title;

    @JsonProperty("type")
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
    private String id;

    @JsonProperty("allowed_paths")
    private List<Object> allowedPaths;

    @JsonProperty("event_provider")
    private List<Object> eventProvider;

    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;

    @JsonProperty("graph_id")
    private String graphId;

    @JsonProperty("version")
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
    private String version_create_dt;

    @JsonProperty("version_changed_by_user")
    private String version_changed_by_user;

    @JsonProperty("multiple")
    private boolean isMultiple;

    @JsonProperty("create_dt")
    private String create_dt;

    @JsonProperty("update_dt")
    private String update_dt;

    @JsonProperty("extra_data")
    private Map<String, String> extraData;

    @JsonProperty("allowed_developers")
    private List<String> allowed_developers;

    @JsonProperty("restricted_developers")
    private List<String> restricted_developers;

    @JsonProperty("version_diff")
    private VersionDiff versionDiff;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getGraphVersionCalculated() {
        return graphVersionCalculated;
    }

    @Override
    public String getDescription() {
        return description;
    }
}