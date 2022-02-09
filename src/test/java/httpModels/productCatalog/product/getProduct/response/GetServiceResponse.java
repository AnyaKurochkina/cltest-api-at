package httpModels.productCatalog.product.getProduct.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetServiceResponse implements GetImpl {

	@JsonProperty("allowed_paths")
	private List<Object> allowedPaths;

	@JsonProperty("is_open")
	private boolean isOpen;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("author")
	private String author;

	@JsonProperty("information_systems")
	private List<Object> informationSystems;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("version_create_dt")
	private String versionCreateDt;

	@JsonProperty("envs")
	private List<String> envs;

	@JsonProperty("description")
	private String description;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("title")
	private String title;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("version")
	private String version;

	@JsonProperty("max_count")
	private int maxCount;

	@JsonProperty("number")
	private int number;

	@JsonProperty("version_changed_by_user")
	private String versionChangedByUser;

	@JsonProperty("name")
	private String name;

	@JsonProperty("restricted_paths")
	private List<Object> restrictedPaths;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("graph_version_pattern")
	private String graphVersionPattern;

	@JsonProperty("id")
	private String id;

	@JsonProperty("graph_version_calculated")
	private String graphVersionCalculated;

	@JsonProperty("category")
	private String category;

	@JsonProperty("last_version")
	private String lastVersion;

	public List<Object> getAllowedPaths(){
		return allowedPaths;
	}

	public boolean isIsOpen(){
		return isOpen;
	}

	public List<String> getVersionList(){
		return versionList;
	}

	public String getAuthor(){
		return author;
	}

	public List<Object> getInformationSystems(){
		return informationSystems;
	}

	public String getIcon(){
		return icon;
	}

	public String getVersionCreateDt(){
		return versionCreateDt;
	}

	public List<String> getEnvs(){
		return envs;
	}

	public String getDescription(){
		return description;
	}

	public String getGraphVersion(){
		return graphVersion;
	}

	public List<Object> getRestrictedGroups(){
		return restrictedGroups;
	}

	public String getTitle(){
		return title;
	}

	public String getGraphId(){
		return graphId;
	}

	public String getVersion(){
		return version;
	}

	public int getMaxCount(){
		return maxCount;
	}

	public int getNumber(){
		return number;
	}

	public String getVersionChangedByUser(){
		return versionChangedByUser;
	}

	public String getName(){
		return name;
	}

	public List<Object> getRestrictedPaths(){
		return restrictedPaths;
	}

	public List<Object> getAllowedGroups(){
		return allowedGroups;
	}

	public String getGraphVersionPattern(){
		return graphVersionPattern;
	}

	public String getId(){
		return id;
	}

	public String getGraphVersionCalculated(){
		return graphVersionCalculated;
	}

	public String getCategory(){
		return category;
	}
}