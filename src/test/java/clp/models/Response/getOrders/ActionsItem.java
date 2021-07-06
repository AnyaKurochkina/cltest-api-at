package clp.models.response.getOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionsItem{

	@JsonProperty("available_without_money")
	private boolean availableWithoutMoney;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("description")
	private String description;

	@JsonProperty("skip_on_prebilling")
	private boolean skipOnPrebilling;

	@JsonProperty("auto_removing_if_failed")
	private boolean autoRemovingIfFailed;

	@JsonProperty("title")
	private String title;

	@JsonProperty("type")
	private String type;

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

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("config_restriction")
	private Object configRestriction;

	@JsonProperty("data_config_key")
	private Object dataConfigKey;

	@JsonProperty("name")
	private String name;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("data_config_fields")
	private List<Object> dataConfigFields;

	@JsonProperty("required_order_statuses")
	private List<String> requiredOrderStatuses;

	public boolean isAvailableWithoutMoney(){
		return availableWithoutMoney;
	}

	public String getIcon(){
		return icon;
	}

	public String getGraphVersion(){
		return graphVersion;
	}

	public String getDescription(){
		return description;
	}

	public boolean isSkipOnPrebilling(){
		return skipOnPrebilling;
	}

	public boolean isAutoRemovingIfFailed(){
		return autoRemovingIfFailed;
	}

	public String getTitle(){
		return title;
	}

	public String getType(){
		return type;
	}

	public List<String> getEventType(){
		return eventType;
	}

	public List<String> getRequiredItemStatuses(){
		return requiredItemStatuses;
	}

	public Object getDataConfigPath(){
		return dataConfigPath;
	}

	public List<Object> getRestrictedPaths(){
		return restrictedPaths;
	}

	public String getGraphVersionPattern(){
		return graphVersionPattern;
	}

	public String getId(){
		return id;
	}

	public List<Object> getAllowedPaths(){
		return allowedPaths;
	}

	public List<String> getEventProvider(){
		return eventProvider;
	}

	public boolean isActive(){
		return active;
	}

	public List<Object> getRestrictedGroups(){
		return restrictedGroups;
	}

	public String getGraphId(){
		return graphId;
	}

	public Object getConfigRestriction(){
		return configRestriction;
	}

	public Object getDataConfigKey(){
		return dataConfigKey;
	}

	public String getName(){
		return name;
	}

	public List<Object> getAllowedGroups(){
		return allowedGroups;
	}

	public List<Object> getDataConfigFields(){
		return dataConfigFields;
	}

	public List<String> getRequiredOrderStatuses(){
		return requiredOrderStatuses;
	}
}