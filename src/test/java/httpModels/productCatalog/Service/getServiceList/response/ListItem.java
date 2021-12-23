package httpModels.productCatalog.Service.getServiceList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListItem{

	@JsonProperty("allowed_paths")
	private List<Object> allowedPaths;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("is_published")
	private Boolean isPublished;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("description")
	private String description;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("version")
	private String version;

	@JsonProperty("data_source")
	private DataSource dataSource;

	@JsonProperty("last_version")
	private String lastVersion;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("direction_id")
	private String directionId;

	@JsonProperty("extra_data")
	private ExtraData extraData;

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

	@JsonProperty("direction")
	private String direction;

	@JsonProperty("graph_version_calculated")
	private String graphVersionCalculated;

	@JsonProperty("title")
	private String title;

}