package httpModels.productCatalog.graphs.getUsedList;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class GetUsedListResponse{

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("graph_version_pattern")
	private String graphVersionPattern;

	@JsonProperty("id")
	private String id;

	@JsonProperty("graph_version_calculated")
	private String graphVersionCalculated;

	@JsonProperty("type")
	private String type;

	@JsonProperty("title")
	private String title;

	@JsonProperty("version")
	private String version;

	@JsonProperty("icon")
	private String icon;

	@JsonIgnore()
	private List<Object> graphs;
}