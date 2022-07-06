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

	@JsonProperty("type")
	private String type;

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("version")
	private String version;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("damage_order_on_error")
	private Boolean damage_order_on_error;

	@JsonProperty("restricted_developers")
	private List<String> restricted_developers;

	@JsonProperty("allowed_developers")
	private List<String> allowed_developers;

	@JsonProperty("current_version")
	private String currentVersion;

	@JsonProperty("lock_order_on_error")
	private Boolean lockOrderOnError;

	@Override
	public String getCreateData() {
		return create_dt;
	}

	@Override
	public String getUpDateData() {
		return update_dt;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}
}