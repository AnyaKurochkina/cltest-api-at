package httpModels.productCatalog.Template.createTemplate.response;

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

	@JsonProperty("priority_can_be_overridden")
	private boolean priorityCanBeOverridden;

	@JsonProperty("log_can_be_overridden")
	private boolean logCanBeOverridden;

	@JsonProperty("timeout")
	private int timeout;

	@JsonProperty("coords_x")
	private double coordsX;

	@JsonProperty("output")
	private Output output;

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
	private Input input;

	@JsonProperty("extra_data")
	private Object extraData;

	@JsonProperty("name")
	private String name;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("additional_output")
	private boolean additionalOutput;

	public boolean isAdditionalInput(){
		return additionalInput;
	}

	public String getColor(){
		return color;
	}

	public List<String> getVersionList(){
		return versionList;
	}

	public String getIcon(){
		return icon;
	}

	public String getDescription(){
		return description;
	}

	public String getRun(){
		return run;
	}

	public boolean isPriorityCanBeOverridden(){
		return priorityCanBeOverridden;
	}

	public boolean isLogCanBeOverridden(){
		return logCanBeOverridden;
	}

	public int getTimeout(){
		return timeout;
	}

	public double getCoordsX(){
		return coordsX;
	}

	public Output getOutput(){
		return output;
	}

	public PrintedOutput getPrintedOutput(){
		return printedOutput;
	}

	public boolean isPrintedOutputCanBeOverridden(){
		return printedOutputCanBeOverridden;
	}

	public List<Object> getRestrictedPaths(){
		return restrictedPaths;
	}

	public int getId(){
		return id;
	}

	public double getCoordsY(){
		return coordsY;
	}

	public String getRollback(){
		return rollback;
	}

	public List<Object> getAllowedPaths(){
		return allowedPaths;
	}

	public String getLogLevel(){
		return logLevel;
	}

	public List<Object> getRestrictedGroups(){
		return restrictedGroups;
	}

	public int getPriority(){
		return priority;
	}

	public String getVersion(){
		return version;
	}

	public String getLastVersion(){
		return lastVersion;
	}

	public Input getInput(){
		return input;
	}

	public Object getExtraData(){
		return extraData;
	}

	public String getName(){
		return name;
	}

	public List<Object> getAllowedGroups(){
		return allowedGroups;
	}

	public boolean isAdditionalOutput(){
		return additionalOutput;
	}
}