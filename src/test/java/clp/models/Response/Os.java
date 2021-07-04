package clp.models.Response;

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

	public void setLocalization(String localization){
		this.localization = localization;
	}

	public String getLocalization(){
		return localization;
	}

	public void setVendor(String vendor){
		this.vendor = vendor;
	}

	public String getVendor(){
		return vendor;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setDistribution(String distribution){
		this.distribution = distribution;
	}

	public String getDistribution(){
		return distribution;
	}

	public void setVersion(String version){
		this.version = version;
	}

	public String getVersion(){
		return version;
	}

	public void setArchitecture(String architecture){
		this.architecture = architecture;
	}

	public String getArchitecture(){
		return architecture;
	}
}