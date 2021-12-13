package httpModels.productCatalog.Service.copyService.response;

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
public class CopyServiceResponse{

	@JsonProperty("allowed_paths")
	private List<String> allowedPaths;

	@JsonProperty("is_published")
	private Boolean isPublished;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("version_create_dt")
	private String versionCreateDt;

	@JsonProperty("description")
	private String description;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("restricted_groups")
	private List<String> restrictedGroups;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("version")
	private String version;

	@JsonProperty("data_source")
	private DataSource dataSource;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("direction_id")
	private String directionId;

	@JsonProperty("extra_data")
	private ExtraData extraData;

	@JsonProperty("version_changed_by_user")
	private String versionChangedByUser;

	@JsonProperty("name")
	private String name;

	@JsonProperty("restricted_paths")
	private List<String> restrictedPaths;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("graph_version_pattern")
	private String graphVersionPattern;

	@JsonProperty("id")
	private String id;

	@JsonProperty("direction")
	private String direction;
}