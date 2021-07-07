package clp.models.response.postOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreviewItemsItem{

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("subtype")
	private String subtype;

	@JsonProperty("provider")
	private String provider;

	@JsonProperty("id")
	private String id;

	@JsonProperty("state")
	private String state;

	@JsonProperty("type")
	private String type;

	@JsonProperty("config")
	private Config config;

	public String getItemId(){
		return itemId;
	}

	public String getSubtype(){
		return subtype;
	}

	public String getProvider(){
		return provider;
	}

	public String getId(){
		return id;
	}

	public String getState(){
		return state;
	}

	public String getType(){
		return type;
	}

	public Config getConfig(){
		return config;
	}
}