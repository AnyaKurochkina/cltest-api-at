package models.cloud.productCatalog.graph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsedListResponse{

	@JsonProperty("name")
	private String name;

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
}