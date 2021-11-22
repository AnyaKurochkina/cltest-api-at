package httpModels.productCatalog.getTemplate.response;

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
public class ListItem{

	@JsonProperty("additional_input")
	private Boolean additionalInput;

	@JsonProperty("color")
	private String color;

	@JsonProperty("icon")
	private String icon;

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

	@JsonProperty("coords_x")
	private Integer coordsX;

	@JsonProperty("output")
	private Output output;

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

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("priority")
	private Integer priority;

	@JsonProperty("input")
	private Input input;

	@JsonProperty("extra_data")
	private Object extraData;

	@JsonProperty("name")
	private String name;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("additional_output")
	private Boolean additionalOutput;
}