package httpModels.productCatalog.template.createTemplate.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CreateTemplateResponse{

	@JsonProperty("additional_input")
	private boolean additionalInput;

	@JsonProperty("color")
	private String color;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("description")
	private String description;

	@JsonProperty("run")
	private String run;

	@JsonProperty("type")
	private String type;

	@JsonProperty("priority_can_be_overridden")
	private boolean priorityCanBeOverridden;

	@JsonProperty("log_can_be_overridden")
	private boolean logCanBeOverridden;

	@JsonProperty("timeout")
	private int timeout;

	@JsonProperty("coords_x")
	private double coordsX;

	@JsonProperty("output")
	private Map<String, Map<String,String>> output;

	@JsonProperty("printed_output")
	private PrintedOutput printedOutput;

	@JsonProperty("printed_output_can_be_overridden")
	private boolean printedOutputCanBeOverridden;

	@JsonProperty("restricted_paths")
	private List<Object> restrictedPaths;

	@JsonProperty("id")
	private int id;

	@JsonProperty("coords_y")
	private double coordsY;

	@JsonProperty("rollback")
	private String rollback;

	@JsonProperty("allowed_paths")
	private List<Object> allowedPaths;

	@JsonProperty("log_level")
	private String logLevel;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("priority")
	private int priority;

	@JsonProperty("version")
	private String version;

	@JsonProperty("last_version")
	private String lastVersion;

	@JsonProperty("input")
	private Map<String,Map<String,String>> input;

	@JsonProperty("extra_data")
	private Object extraData;

	@JsonProperty("name")
	private String name;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("additional_output")
	private boolean additionalOutput;

	@JsonProperty("version_create_dt")
	private String version_create_dt;

	@JsonProperty("version_changed_by_user")
	private String version_changed_by_user;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("title")
	private String title;

	@JsonProperty("current_version")
	private String currentVersion;
}