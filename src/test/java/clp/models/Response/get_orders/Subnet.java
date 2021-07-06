package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subnet{

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