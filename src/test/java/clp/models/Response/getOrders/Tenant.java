package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tenant{

	@JsonProperty("name")
	private String name;

	@JsonProperty("uuid")
	private String uuid;

	public String getName(){
		return name;
	}

	public String getUuid(){
		return uuid;
	}
}