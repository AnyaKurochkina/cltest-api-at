package httpModels.productCatalog.template.getTemplate.response;

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

public class GetTemplateResponse implements GetImpl {

	@JsonProperty("additional_input")
	private Boolean additionalInput;

	@JsonProperty("title")
	private String title;

	@JsonProperty("color")
	private String color;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("icon_url")
	private String iconUrl;

	@JsonProperty("icon_store_id")
	private String iconStoreId;

	@JsonProperty("type")
	private String type;

	@JsonProperty("description")
	private String description;

	@JsonProperty("run")
	private String run;

	@JsonProperty("priority_can_be_overridden")
	private Boolean priorityCanBeOverridden;

	@JsonProperty("log_can_be_overridden")
	private Boolean logCanBeOverridden;

	@JsonProperty("timeout")
	private Integer timeout;

	@JsonProperty("output")
	private Output output;

	@JsonProperty("coords_x")
	private Integer coordsX;

	@JsonProperty("printed_output")
	private PrintedOutput printedOutput;

	@JsonProperty("printed_output_can_be_overridden")
	private Boolean printedOutputCanBeOverridden;

	@JsonProperty("restricted_paths")
	private List<Object> restrictedPaths;

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("coords_y")
	private Integer coordsY;

	@JsonProperty("rollback")
	private Object rollback;

	@JsonProperty("allowed_paths")
	private List<Object> allowedPaths;

	@JsonProperty("log_level")
	private Object logLevel;

	@JsonProperty("version_create_dt")
	private String versionCreateDt;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("priority")
	private Integer priority;

	@JsonProperty("version")
	private String version;

	@JsonProperty("input")
	private Input input;

	@JsonProperty("extra_data")
	private Object extraData;

	@JsonProperty("version_changed_by_user")
	private String versionChangedByUser;

	@JsonProperty("name")
	private String name;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("additional_output")
	private Boolean additionalOutput;

	@JsonProperty("last_version")
	private String last_version;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("current_version")
	private String currentVersion;

	@JsonProperty("icon_base64")
	private String iconBase64;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getGraphVersionCalculated() {
		return null;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getId() {
		return String.valueOf(id);
	}
}