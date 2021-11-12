package models.productCatalog.getGraphs.response;

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

	@JsonProperty("last_version")
	private String lastVersion;

	@JsonProperty("product_ids")
	private List<Object> productIds;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("action_ids")
	private List<Object> actionIds;

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;
}