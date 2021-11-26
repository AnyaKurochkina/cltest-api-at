package httpModels.productCatalog.getTemplate.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListItem{

	@JsonProperty("additional_input")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean additionalInput;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("color")
	private String color;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("icon")
	private String icon;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("description")
	private String description;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("run")
	private String run;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("priority_can_be_overridden")
	private Boolean priorityCanBeOverridden;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("log_can_be_overridden")
	private Boolean logCanBeOverridden;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("timeout")
	private Integer timeout;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("coords_x")
	private Double coordsX;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("output")
	private Output output;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("printed_output")
	private PrintedOutput printedOutput;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("printed_output_can_be_overridden")
	private Boolean printedOutputCanBeOverridden;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("restricted_paths")
	private List<String> restrictedPaths;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("id")
	private Integer id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("coords_y")
	private Double coordsY;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("rollback")
	private String rollback;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("allowed_paths")
	private List<String> allowedPaths;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("log_level")
	private String logLevel;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("restricted_groups")
	private List<String> restrictedGroups;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("priority")
	private Integer priority;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("input")
	private Input input;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("extra_data")
	private ExtraData extraData;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("name")
	private String name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("allowed_groups")
	private List<String> allowedGroups;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("additional_output")
	private Boolean additionalOutput;
}