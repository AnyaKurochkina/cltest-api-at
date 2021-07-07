package clp.models.response.postOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Params{

	@JsonProperty("image")
	private Image image;

	@JsonProperty("os_version")
	private String osVersion;

	@JsonProperty("ad_integration")
	private boolean adIntegration;

	@JsonProperty("extra_nics")
	private List<Object> extraNics;

	@JsonProperty("data_center")
	private DataCenter dataCenter;

	@JsonProperty("code_apd")
	private String codeApd;

	@JsonProperty("resource_pool")
	private ResourcePool resourcePool;

	@JsonProperty("ad_logon_grants")
	private List<AdLogonGrantsItem> adLogonGrants;

	@JsonProperty("environment_type")
	private String environmentType;

	@JsonProperty("platform")
	private String platform;

	@JsonProperty("boot_disk")
	private BootDisk bootDisk;

	@JsonProperty("on_support")
	private boolean onSupport;

	@JsonProperty("flavor")
	private Flavor flavor;

	@JsonProperty("environment")
	private String environment;

	@JsonProperty("default_nic")
	private DefaultNic defaultNic;

	@JsonProperty("env_prefix")
	private String envPrefix;

	@JsonProperty("extra_mounts")
	private List<ExtraMountsItem> extraMounts;

	@JsonProperty("domain")
	private String domain;

	@JsonProperty("is_code")
	private String isCode;

	@JsonProperty("ris_id")
	private int risId;

	public Image getImage(){
		return image;
	}

	public String getOsVersion(){
		return osVersion;
	}

	public boolean isAdIntegration(){
		return adIntegration;
	}

	public List<Object> getExtraNics(){
		return extraNics;
	}

	public DataCenter getDataCenter(){
		return dataCenter;
	}

	public String getCodeApd(){
		return codeApd;
	}

	public ResourcePool getResourcePool(){
		return resourcePool;
	}

	public List<AdLogonGrantsItem> getAdLogonGrants(){
		return adLogonGrants;
	}

	public String getEnvironmentType(){
		return environmentType;
	}

	public String getPlatform(){
		return platform;
	}

	public BootDisk getBootDisk(){
		return bootDisk;
	}

	public boolean isOnSupport(){
		return onSupport;
	}

	public Flavor getFlavor(){
		return flavor;
	}

	public String getEnvironment(){
		return environment;
	}

	public DefaultNic getDefaultNic(){
		return defaultNic;
	}

	public String getEnvPrefix(){
		return envPrefix;
	}

	public List<ExtraMountsItem> getExtraMounts(){
		return extraMounts;
	}

	public String getDomain(){
		return domain;
	}

	public String getIsCode(){
		return isCode;
	}

	public int getRisId(){
		return risId;
	}
}