package clp.models.response.getOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Config{

	@JsonProperty("dbs")
	private List<Object> dbs;

	@JsonProperty("connection_url")
	private String connectionUrl;

	@JsonProperty("configuration")
	private Configuration configuration;

	@JsonProperty("db_owners")
	private List<Object> dbOwners;

	@JsonProperty("db_users")
	private List<Object> dbUsers;

	@JsonProperty("version")
	private String version;

	@JsonProperty("image")
	private Image image;

	@JsonProperty("os_version")
	private String osVersion;

	@JsonProperty("default_v6_address")
	private String defaultV6Address;

	@JsonProperty("ad_integration")
	private boolean adIntegration;

	@JsonProperty("mounts")
	private List<MountsItem> mounts;

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

	@JsonProperty("hostname")
	private String hostname;

	@JsonProperty("environment")
	private String environment;

	@JsonProperty("default_nic")
	private DefaultNic defaultNic;

	@JsonProperty("extra_mounts")
	private List<ExtraMountsItem> extraMounts;

	@JsonProperty("domain")
	private String domain;

	@JsonProperty("swap_size")
	private int swapSize;

	@JsonProperty("default_v4_address")
	private String defaultV4Address;

	@JsonProperty("extra_disks")
	private List<ExtraDisksItem> extraDisks;

	@JsonProperty("tenant")
	private Tenant tenant;

	public List<Object> getDbs(){
		return dbs;
	}

	public String getConnectionUrl(){
		return connectionUrl;
	}

	public Configuration getConfiguration(){
		return configuration;
	}

	public List<Object> getDbOwners(){
		return dbOwners;
	}

	public List<Object> getDbUsers(){
		return dbUsers;
	}

	public String getVersion(){
		return version;
	}

	public Image getImage(){
		return image;
	}

	public String getOsVersion(){
		return osVersion;
	}

	public String getDefaultV6Address(){
		return defaultV6Address;
	}

	public boolean isAdIntegration(){
		return adIntegration;
	}

	public List<MountsItem> getMounts(){
		return mounts;
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

	public String getHostname(){
		return hostname;
	}

	public String getEnvironment(){
		return environment;
	}

	public DefaultNic getDefaultNic(){
		return defaultNic;
	}

	public List<ExtraMountsItem> getExtraMounts(){
		return extraMounts;
	}

	public String getDomain(){
		return domain;
	}

	public int getSwapSize(){
		return swapSize;
	}

	public String getDefaultV4Address(){
		return defaultV4Address;
	}

	public List<ExtraDisksItem> getExtraDisks(){
		return extraDisks;
	}

	public Tenant getTenant(){
		return tenant;
	}
}