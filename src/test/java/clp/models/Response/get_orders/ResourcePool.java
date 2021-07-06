package clp.models.response.get_orders;

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

	public String getEndpoint(){
		return endpoint;
	}

	public String getTenantPrefix(){
		return tenantPrefix;
	}

	public String getName(){
		return name;
	}

	public String getUuid(){
		return uuid;
	}

	public String getPlatform(){
		return platform;
	}
}