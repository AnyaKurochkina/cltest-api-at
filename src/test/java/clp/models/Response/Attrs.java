package clp.models.Response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Attrs{

	@JsonProperty("creator")
	private Creator creator;

	@JsonProperty("preview_items")
	private List<PreviewItemsItem> previewItems;

	@JsonProperty("tariff_plan_id")
	private String tariffPlanId;

	@JsonProperty("environment_prefix_id")
	private String environmentPrefixId;

	@JsonProperty("os_version")
	private String osVersion;

	@JsonProperty("ad_integration")
	private boolean adIntegration;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("extra_nics")
	private List<Object> extraNics;

	@JsonProperty("data_center")
	private String dataCenter;

	@JsonProperty("organization_name")
	private String organizationName;

	@JsonProperty("ad_logon_grants")
	private List<AdLogonGrantsItem> adLogonGrants;

	@JsonProperty("platform")
	private String platform;

	@JsonProperty("boot_disk")
	private BootDisk bootDisk;

	@JsonProperty("on_support")
	private boolean onSupport;

	@JsonProperty("product_title")
	private String productTitle;

	@JsonProperty("flavor")
	private Flavor flavor;

	@JsonProperty("project_environment_id")
	private String projectEnvironmentId;

	@JsonProperty("folder")
	private String folder;

	@JsonProperty("account_id")
	private String accountId;

	@JsonProperty("default_nic")
	private DefaultNic defaultNic;

	@JsonProperty("project_path")
	private String projectPath;

	@JsonProperty("extra_mounts")
	private List<ExtraMountsItem> extraMounts;

	@JsonProperty("domain")
	private String domain;

	@JsonProperty("information_system_id")
	private String informationSystemId;

	public void setCreator(Creator creator){
		this.creator = creator;
	}

	public Creator getCreator(){
		return creator;
	}

	public void setPreviewItems(List<PreviewItemsItem> previewItems){
		this.previewItems = previewItems;
	}

	public List<PreviewItemsItem> getPreviewItems(){
		return previewItems;
	}

	public void setTariffPlanId(String tariffPlanId){
		this.tariffPlanId = tariffPlanId;
	}

	public String getTariffPlanId(){
		return tariffPlanId;
	}

	public void setEnvironmentPrefixId(String environmentPrefixId){
		this.environmentPrefixId = environmentPrefixId;
	}

	public String getEnvironmentPrefixId(){
		return environmentPrefixId;
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

	public void setGraphVersion(String graphVersion){
		this.graphVersion = graphVersion;
	}

	public String getGraphVersion(){
		return graphVersion;
	}

	public void setExtraNics(List<Object> extraNics){
		this.extraNics = extraNics;
	}

	public List<Object> getExtraNics(){
		return extraNics;
	}

	public void setDataCenter(String dataCenter){
		this.dataCenter = dataCenter;
	}

	public String getDataCenter(){
		return dataCenter;
	}

	public void setOrganizationName(String organizationName){
		this.organizationName = organizationName;
	}

	public String getOrganizationName(){
		return organizationName;
	}

	public void setAdLogonGrants(List<AdLogonGrantsItem> adLogonGrants){
		this.adLogonGrants = adLogonGrants;
	}

	public List<AdLogonGrantsItem> getAdLogonGrants(){
		return adLogonGrants;
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

	public void setProductTitle(String productTitle){
		this.productTitle = productTitle;
	}

	public String getProductTitle(){
		return productTitle;
	}

	public void setFlavor(Flavor flavor){
		this.flavor = flavor;
	}

	public Flavor getFlavor(){
		return flavor;
	}

	public void setProjectEnvironmentId(String projectEnvironmentId){
		this.projectEnvironmentId = projectEnvironmentId;
	}

	public String getProjectEnvironmentId(){
		return projectEnvironmentId;
	}

	public void setFolder(String folder){
		this.folder = folder;
	}

	public String getFolder(){
		return folder;
	}

	public void setAccountId(String accountId){
		this.accountId = accountId;
	}

	public String getAccountId(){
		return accountId;
	}

	public void setDefaultNic(DefaultNic defaultNic){
		this.defaultNic = defaultNic;
	}

	public DefaultNic getDefaultNic(){
		return defaultNic;
	}

	public void setProjectPath(String projectPath){
		this.projectPath = projectPath;
	}

	public String getProjectPath(){
		return projectPath;
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

	public void setInformationSystemId(String informationSystemId){
		this.informationSystemId = informationSystemId;
	}

	public String getInformationSystemId(){
		return informationSystemId;
	}
}