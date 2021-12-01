package httpModels.productCatalog.Product.getProducts.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListItem{

	@JsonProperty("allowed_paths")
	private List<Object> allowedPaths;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("is_open")
	private Boolean isOpen;

	@JsonProperty("author")
	private String author;

	@JsonProperty("information_systems")
	private List<Object> informationSystems;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("description")
	private String description;

	@JsonProperty("envs")
	private List<String> envs;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("title")
	private String title;

	@JsonProperty("version")
	private String version;

	@JsonProperty("max_count")
	private Integer maxCount;

	@JsonProperty("last_version")
	private String lastVersion;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("name")
	private String name;

	@JsonProperty("restricted_paths")
	private List<Object> restrictedPaths;

	@JsonProperty("graph_version_pattern")
	private String graphVersionPattern;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("id")
	private String id;

	@JsonProperty("category")
	private String category;
}