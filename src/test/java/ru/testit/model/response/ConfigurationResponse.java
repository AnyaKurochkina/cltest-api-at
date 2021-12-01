package ru.testit.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationResponse{
	private boolean isDefault;
	private Map<String, String> capabilities;
	private String name;
	private String id;

	public void setIsDefault(boolean isDefault){
		this.isDefault = isDefault;
	}

	public boolean isIsDefault(){
		return isDefault;
	}

	public void setCapabilities(Map<String, String> capabilities){
		this.capabilities = capabilities;
	}

	public Map<String, String> getCapabilities(){
		return capabilities;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}
}
