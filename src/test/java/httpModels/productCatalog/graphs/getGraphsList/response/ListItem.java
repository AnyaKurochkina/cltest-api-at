package httpModels.productCatalog.graphs.getGraphsList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ItemImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ListItem implements ItemImpl {

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

	@JsonProperty("version")
	private String version;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}
}