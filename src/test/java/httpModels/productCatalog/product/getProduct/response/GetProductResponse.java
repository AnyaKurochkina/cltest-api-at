package httpModels.productCatalog.product.getProduct.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductResponse implements GetImpl {

	@JsonProperty("allowed_paths")
	private List<Object> allowedPaths;

	@JsonProperty("is_open")
	private boolean isOpen;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("author")
	private String author;

	@JsonProperty("current_version")
	private String currentVersion;

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

	@JsonProperty("info")
	private Map<String, String> info;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("extra_data")
	private Map<String, String> extraData;

	@JsonProperty("in_general_list")
	private Boolean inGeneralList;

	@JsonProperty("allowed_developers")
	private String allowed_developers;

	@JsonProperty("restricted_developers")
	private String restricted_developers;
	private String payment;
}