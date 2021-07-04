package clp.models.Response;

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

	public void setItemId(String itemId){
		this.itemId = itemId;
	}

	public String getItemId(){
		return itemId;
	}

	public void setSubtype(String subtype){
		this.subtype = subtype;
	}

	public String getSubtype(){
		return subtype;
	}

	public void setProvider(String provider){
		this.provider = provider;
	}

	public String getProvider(){
		return provider;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setConfig(Config config){
		this.config = config;
	}

	public Config getConfig(){
		return config;
	}
}