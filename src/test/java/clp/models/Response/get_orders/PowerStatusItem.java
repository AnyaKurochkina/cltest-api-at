package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerStatusItem{

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("status")
	private String status;

	public String getItemId(){
		return itemId;
	}

	public String getStatus(){
		return status;
	}
}