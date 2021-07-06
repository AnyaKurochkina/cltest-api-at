package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreviewItemsItem{

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("provider")
	private String provider;

	@JsonProperty("action_id")
	private String actionId;

	@JsonProperty("update_dt")
	private String updateDt;

	@JsonProperty("created_row_dt")
	private String createdRowDt;

	@JsonProperty("state")
	private String state;

	@JsonProperty("type")
	private String type;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("config")
	private Config config;

	@JsonProperty("order_id")
	private String orderId;

	@JsonProperty("parent")
	private String parent;

	@JsonProperty("subtype")
	private String subtype;

	public String getItemId(){
		return itemId;
	}

	public String getProvider(){
		return provider;
	}

	public String getActionId(){
		return actionId;
	}

	public String getUpdateDt(){
		return updateDt;
	}

	public String getCreatedRowDt(){
		return createdRowDt;
	}

	public String getState(){
		return state;
	}

	public String getType(){
		return type;
	}

	public String getGraphId(){
		return graphId;
	}

	public Config getConfig(){
		return config;
	}

	public String getOrderId(){
		return orderId;
	}

	public String getParent(){
		return parent;
	}

	public String getSubtype(){
		return subtype;
	}
}