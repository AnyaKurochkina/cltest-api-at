package httpModels.productCatalog.graphs.createGraph.response;

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
public class CreateGraphResponse{

	@JsonProperty("last_version")
	private String lastVersion;

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("author")
	private String author;

	@JsonProperty("json_schema")
	private JsonSchema jsonSchema;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("static_data")
	private StaticData staticData;

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("type")
	private String type;

	@JsonProperty("version")
	private String version;

	@JsonProperty("ui_schema")
	private UiSchema uiSchema;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("is_sequential")
	private Boolean isSequential;

	@JsonProperty("allowed_developers")
	private String allowed_developers;

	@JsonProperty("restricted_developers")
	private String restricted_developers;

	@JsonProperty("damage_order_on_error")
	private Boolean damage_order_on_error;
}