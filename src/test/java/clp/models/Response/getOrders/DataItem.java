package clp.models.response.getOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataItem{

	@JsonProperty("virtualization")
	private Object virtualization;

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("state")
	private String state;

	@JsonProperty("type")
	private String type;

	@JsonProperty("actions")
	private List<ActionsItem> actions;

	@JsonProperty("config")
	private Config config;

	public Object getVirtualization(){
		return virtualization;
	}

	public String getItemId(){
		return itemId;
	}

	public String getState(){
		return state;
	}

	public String getType(){
		return type;
	}

	public List<ActionsItem> getActions(){
		return actions;
	}

	public Config getConfig(){
		return config;
	}
}