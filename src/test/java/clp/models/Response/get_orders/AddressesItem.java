package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressesItem{

	@JsonProperty("address")
	private String address;

	@JsonProperty("type")
	private String type;

	public String getAddress(){
		return address;
	}

	public String getType(){
		return type;
	}
}