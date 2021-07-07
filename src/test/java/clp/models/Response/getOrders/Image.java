package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image{

	@JsonProperty("os")
	private Os os;

	@JsonProperty("size")
	private int size;

	@JsonProperty("name")
	private String name;

	@JsonProperty("uuid")
	private String uuid;

	public Os getOs(){
		return os;
	}

	public int getSize(){
		return size;
	}

	public String getName(){
		return name;
	}

	public String getUuid(){
		return uuid;
	}
}