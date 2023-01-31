package httpModels.productCatalog.service.getService.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetServiceResponse implements GetImpl {

	@JsonProperty("turn_off_inventory")
	private Boolean turnOffInventory;

	@JsonProperty("current_version")
	private String currentVersion;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("is_published")
	private Boolean isPublished;

	@JsonProperty("icon_url")
	private String iconUrl;

	@JsonProperty("icon_store_id")
	private String iconStoreId;

	@JsonProperty("description")
	private String description;

	@JsonProperty("service_info")
	private String serviceInfo;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("title")
	private String title;

	@JsonProperty("direction_id")
	private String directionId;

	@JsonProperty("inventory_actions")
	private List<Object> inventoryActions;

	@JsonProperty("graph_version_pattern")
	private String graphVersionPattern;

	@JsonProperty("hide_node_name_output")
	private Boolean hideNodeNameOutput;

	@JsonProperty("id")
	private String id;

	@JsonProperty("start_btn_label")
	private String startBtnLabel;

	@JsonProperty("version_create_dt")
	private String versionCreateDt;

	@JsonProperty("restricted_groups")
	private List<String> restrictedGroups;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("version")
	private String version;

	@JsonProperty("data_source")
	private DataSource dataSource;

	@JsonProperty("check_rules")
	private List<Object> checkRules;

	@JsonProperty("auto_open_form")
	private Boolean autoOpenForm;

	@JsonProperty("last_version")
	private String lastVersion;

	@JsonProperty("extra_data")
	private ExtraData extraData;

	@JsonProperty("version_changed_by_user")
	private String versionChangedByUser;

	@JsonProperty("name")
	private String name;

	@JsonProperty("allowed_groups")
	private List<String> allowedGroups;

	@JsonProperty("version_fields")
	private List<String> versionFields;

	@JsonProperty("graph_version_calculated")
	private String graphVersionCalculated;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("auto_open_results")
	private Boolean autoOpenResults;

	@JsonProperty("direction_title")
	private String directionTitle;

	@JsonProperty("allowed_developers")
	private List<String> allowed_developers;

	@JsonProperty("restricted_developers")
	private List<String> restricted_developers;

	private String direction_name;
}