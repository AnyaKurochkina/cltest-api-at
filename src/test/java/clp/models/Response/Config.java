package clp.models.Response;

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

	public void setImage(Image image){
		this.image = image;
	}

	public Image getImage(){
		return image;
	}

	public void setOsVersion(String osVersion){
		this.osVersion = osVersion;
	}

	public String getOsVersion(){
		return osVersion;
	}

	public void setAdIntegration(boolean adIntegration){
		this.adIntegration = adIntegration;
	}

	public boolean isAdIntegration(){
		return adIntegration;
	}

	public void setExtraNics(List<Object> extraNics){
		this.extraNics = extraNics;
	}

	public List<Object> getExtraNics(){
		return extraNics;
	}

	public void setResourcePool(ResourcePool resourcePool){
		this.resourcePool = resourcePool;
	}

	public ResourcePool getResourcePool(){
		return resourcePool;
	}

	public void setEnvironmentType(String environmentType){
		this.environmentType = environmentType;
	}

	public String getEnvironmentType(){
		return environmentType;
	}

	public void setBootDisk(BootDisk bootDisk){
		this.bootDisk = bootDisk;
	}

	public BootDisk getBootDisk(){
		return bootDisk;
	}

	public void setOnSupport(boolean onSupport){
		this.onSupport = onSupport;
	}

	public boolean isOnSupport(){
		return onSupport;
	}

	public void setFlavor(Flavor flavor){
		this.flavor = flavor;
	}

	public Flavor getFlavor(){
		return flavor;
	}

	public void setEnvironment(String environment){
		this.environment = environment;
	}

	public String getEnvironment(){
		return environment;
	}

	public void setDefaultNic(DefaultNic defaultNic){
		this.defaultNic = defaultNic;
	}

	public DefaultNic getDefaultNic(){
		return defaultNic;
	}

	public void setDomain(String domain){
		this.domain = domain;
	}

	public String getDomain(){
		return domain;
	}

	public void setExtraDisks(List<ExtraDisksItem> extraDisks){
		this.extraDisks = extraDisks;
	}

	public List<ExtraDisksItem> getExtraDisks(){
		return extraDisks;
	}

	public void setTenant(Tenant tenant){
		this.tenant = tenant;
	}

	public Tenant getTenant(){
		return tenant;
	}
}