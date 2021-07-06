package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BootDisk{

	@JsonProperty("size")
	private int size;

	@JsonProperty("path")
	private String path;

	@JsonProperty("uuid")
	private String uuid;

	public int getSize(){
		return size;
	}

	public String getPath(){
		return path;
	}

	public String getUuid(){
		return uuid;
	}
}