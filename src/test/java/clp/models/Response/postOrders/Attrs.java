package clp.models.response.postOrders;

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

	public Creator getCreator(){
		return creator;
	}

	public List<PreviewItemsItem> getPreviewItems(){
		return previewItems;
	}

	public String getTariffPlanId(){
		return tariffPlanId;
	}

	public String getEnvironmentPrefixId(){
		return environmentPrefixId;
	}

	public String getOsVersion(){
		return osVersion;
	}

	public boolean isAdIntegration(){
		return adIntegration;
	}

	public String getGraphVersion(){
		return graphVersion;
	}

	public List<Object> getExtraNics(){
		return extraNics;
	}

	public String getDataCenter(){
		return dataCenter;
	}

	public String getOrganizationName(){
		return organizationName;
	}

	public List<AdLogonGrantsItem> getAdLogonGrants(){
		return adLogonGrants;
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

	public String getProductTitle(){
		return productTitle;
	}

	public Flavor getFlavor(){
		return flavor;
	}

	public String getProjectEnvironmentId(){
		return projectEnvironmentId;
	}

	public String getFolder(){
		return folder;
	}

	public String getAccountId(){
		return accountId;
	}

	public DefaultNic getDefaultNic(){
		return defaultNic;
	}

	public String getProjectPath(){
		return projectPath;
	}

	public List<ExtraMountsItem> getExtraMounts(){
		return extraMounts;
	}

	public String getDomain(){
		return domain;
	}

	public String getInformationSystemId(){
		return informationSystemId;
	}
}