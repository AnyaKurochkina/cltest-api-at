package clp.models.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataCenter{

	@JsonProperty("code")
	private String code;

	@JsonProperty("name")
	private String name;

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}