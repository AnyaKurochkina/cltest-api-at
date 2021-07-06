package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Os{

	@JsonProperty("localization")
	private String localization;

	@JsonProperty("vendor")
	private String vendor;

	@JsonProperty("type")
	private String type;

	@JsonProperty("distribution")
	private String distribution;

	@JsonProperty("version")
	private String version;

	@JsonProperty("architecture")
	private String architecture;

	public String getLocalization(){
		return localization;
	}

	public String getVendor(){
		return vendor;
	}

	public String getType(){
		return type;
	}

	public String getDistribution(){
		return distribution;
	}

	public String getVersion(){
		return version;
	}

	public String getArchitecture(){
		return architecture;
	}
}