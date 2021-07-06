package clp.models.response.postOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataCenter{

	@JsonProperty("code")
	private String code;

	@JsonProperty("name")
	private String name;

	public String getCode(){
		return code;
	}

	public String getName(){
		return name;
	}
}