package httpModels.productCatalog.action.getActionList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ItemImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ListItem implements ItemImpl {

    @JsonProperty("available_without_money")
    private Boolean availableWithoutMoney;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("current_version")
    private String currentVersion;

    @JsonProperty("version_list")
    private List<String> versionList;

    @JsonProperty("icon")
    private String icon;

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
    private Object type;

    @JsonProperty("event_type")
    private List<String> eventType;

    @JsonProperty("required_item_statuses")
    private List<String> requiredItemStatuses;

    @JsonProperty("data_config_path")
    private Object dataConfigPath;

    @JsonProperty("restricted_paths")
    private List<Object> restrictedPaths;

    @JsonProperty("graph_version_pattern")
    private String graphVersionPattern;

    @JsonProperty("id")
    private String id;

    @JsonProperty("allowed_paths")
    private List<Object> allowedPaths;

    @JsonProperty("event_provider")
    private List<String> eventProvider;

    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;

    @JsonProperty("graph_id")
    private String graphId;

    @JsonProperty("version")
    private String version;

    @JsonProperty("last_version")
    private String lastVersion;

    @JsonProperty("config_restriction")
    private Object configRestriction;

    @JsonProperty("data_config_key")
    private Object dataConfigKey;

    @JsonProperty("name")
    private String name;

    @JsonProperty("allowed_groups")
    private List<Object> allowedGroups;

    @JsonProperty("graph_version_calculated")
    private String graphVersionCalculated;

    @JsonProperty("data_config_fields")
    private List<Object> dataConfigFields;

    @JsonProperty("required_order_statuses")
    private List<String> requiredOrderStatuses;

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
    private String allowed_developers;

    @JsonProperty("restricted_developers")
    private String restricted_developers;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCreateData() {
        return create_dt;
    }

    @Override
    public String getUpDateData() {
        return update_dt;
    }
}