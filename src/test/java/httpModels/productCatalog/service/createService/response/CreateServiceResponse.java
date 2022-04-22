package httpModels.productCatalog.service.createService.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateServiceResponse{

	@JsonProperty("turn_off_inventory")
	private Boolean turnOffInventory;

	@JsonProperty("current_version")
	private String currentVersion;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("is_published")
	private Boolean isPublished;

	@JsonProperty("icon")
	private String icon;

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

	@JsonProperty("direction")
	private String direction;

	@JsonProperty("start_btn_label")
	private Object startBtnLabel;

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

	@JsonProperty("graph_version_calculated")
	private String graphVersionCalculated;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;
}