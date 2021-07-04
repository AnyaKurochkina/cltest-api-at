package clp.models.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourcePool{

	@JsonProperty("endpoint")
	private String endpoint;

	@JsonProperty("tenant_prefix")
	private String tenantPrefix;

	@JsonProperty("name")
	private String name;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("platform")
	private String platform;

	public void setEndpoint(String endpoint){
		this.endpoint = endpoint;
	}

	public String getEndpoint(){
		return endpoint;
	}

	public void setTenantPrefix(String tenantPrefix){
		this.tenantPrefix = tenantPrefix;
	}

	public String getTenantPrefix(){
		return tenantPrefix;
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

	public void setPlatform(String platform){
		this.platform = platform;
	}

	public String getPlatform(){
		return platform;
	}
}