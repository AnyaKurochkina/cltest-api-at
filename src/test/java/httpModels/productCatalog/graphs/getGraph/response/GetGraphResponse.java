package httpModels.productCatalog.graphs.getGraph.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGraphResponse implements GetImpl {

	@JsonProperty("version_list")
	private List<String> versionList;

	@JsonProperty("author")
	private String author;

	@JsonProperty("version_create_dt")
	private String versionCreateDt;

	@JsonProperty("description")
	private String description;

	@JsonProperty("damage_order_on_error")
	private Boolean damageOrderOnError;

	@JsonProperty("type")
	private String type;

	@JsonProperty("title")
	private String title;

	@JsonProperty("version")
	private String version;

	@JsonProperty("last_version")
	private String last_version;

	@JsonProperty("graph")
	private List<GraphItem> graph;

	@JsonProperty("output")
	private Output output;

	@JsonProperty("printed_output")
	private PrintedOutput printedOutput;

	@JsonProperty("json_schema")
	private JsonSchema jsonSchema;

	@JsonProperty("version_changed_by_user")
	private String versionChangedByUser;

	@JsonProperty("name")
	private String name;

	@JsonProperty("static_data")
	private StaticData staticData;

	@JsonProperty("id")
	private String id;

	@JsonProperty("ui_schema")
	private UiSchema uiSchema;

	@JsonProperty("modifications")
	private List<Object> modifications;

	@Override
	public String getGraphVersionCalculated() {
		return null;
	}
}