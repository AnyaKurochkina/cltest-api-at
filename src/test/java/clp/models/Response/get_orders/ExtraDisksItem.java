package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtraDisksItem{

	@JsonProperty("path")
	private String path;

	@JsonProperty("size")
	private int size;

	@JsonProperty("uuid")
	private String uuid;

	public String getPath(){
		return path;
	}

	public int getSize(){
		return size;
	}

	public String getUuid(){
		return uuid;
	}
}