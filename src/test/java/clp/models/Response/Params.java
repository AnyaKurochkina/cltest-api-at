package clp.models.Response;

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

	public void setDataCenter(DataCenter dataCenter){
		this.dataCenter = dataCenter;
	}

	public DataCenter getDataCenter(){
		return dataCenter;
	}

	public void setCodeApd(String codeApd){
		this.codeApd = codeApd;
	}

	public String getCodeApd(){
		return codeApd;
	}

	public void setResourcePool(ResourcePool resourcePool){
		this.resourcePool = resourcePool;
	}

	public ResourcePool getResourcePool(){
		return resourcePool;
	}

	public void setAdLogonGrants(List<AdLogonGrantsItem> adLogonGrants){
		this.adLogonGrants = adLogonGrants;
	}

	public List<AdLogonGrantsItem> getAdLogonGrants(){
		return adLogonGrants;
	}

	public void setEnvironmentType(String environmentType){
		this.environmentType = environmentType;
	}

	public String getEnvironmentType(){
		return environmentType;
	}

	public void setPlatform(String platform){
		this.platform = platform;
	}

	public String getPlatform(){
		return platform;
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

	public void setEnvPrefix(String envPrefix){
		this.envPrefix = envPrefix;
	}

	public String getEnvPrefix(){
		return envPrefix;
	}

	public void setExtraMounts(List<ExtraMountsItem> extraMounts){
		this.extraMounts = extraMounts;
	}

	public List<ExtraMountsItem> getExtraMounts(){
		return extraMounts;
	}

	public void setDomain(String domain){
		this.domain = domain;
	}

	public String getDomain(){
		return domain;
	}

	public void setIsCode(String isCode){
		this.isCode = isCode;
	}

	public String getIsCode(){
		return isCode;
	}

	public void setRisId(int risId){
		this.risId = risId;
	}

	public int getRisId(){
		return risId;
	}
}