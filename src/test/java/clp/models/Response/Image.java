package clp.models.Response;

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

	public void setOs(Os os){
		this.os = os;
	}

	public Os getOs(){
		return os;
	}

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}
}