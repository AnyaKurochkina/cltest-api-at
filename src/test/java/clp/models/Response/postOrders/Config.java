package clp.models.response.postOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Config{

	@JsonProperty("image")
	private Image image;

	@JsonProperty("os_version")
	private String osVersion;

	@JsonProperty("ad_integration")
	private boolean adIntegration;

	@JsonProperty("extra_nics")
	private List<Object> extraNics;

	@JsonProperty("resource_pool")
	private ResourcePool resourcePool;

	@JsonProperty("environment_type")
	private String environmentType;

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

	@JsonProperty("domain")
	private String domain;

	@JsonProperty("extra_disks")
	private List<ExtraDisksItem> extraDisks;

	@JsonProperty("tenant")
	private Tenant tenant;

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

	public ResourcePool getResourcePool(){
		return resourcePool;
	}

	public String getEnvironmentType(){
		return environmentType;
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

	public String getDomain(){
		return domain;
	}

	public List<ExtraDisksItem> getExtraDisks(){
		return extraDisks;
	}

	public Tenant getTenant(){
		return tenant;
	}
}