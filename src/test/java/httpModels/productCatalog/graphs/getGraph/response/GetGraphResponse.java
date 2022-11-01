package httpModels.productCatalog.graphs.getGraph.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.productCatalog.graph.GraphItem;
import models.productCatalog.graph.Modification;

import java.util.List;
import java.util.Map;

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
	private Map<String, String> output;

	@JsonProperty("printed_output")
	private PrintedOutput printedOutput;

	@JsonProperty("json_schema")
	private Map<String, Object> jsonSchema;

	@JsonProperty("version_changed_by_user")
	private String versionChangedByUser;

	@JsonProperty("name")
	private String name;

	@JsonProperty("static_data")
	private Map<String, Object> staticData;

	@JsonProperty("id")
	private String id;

	@JsonProperty("ui_schema")
	private Map<String, Object> uiSchema;

	@JsonProperty("modifications")
	private List<Modification> modifications;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("is_sequential")
	private Boolean isSequential;

	@JsonProperty("allowed_developers")
	private List<String> allowedDevelopers;

	@JsonProperty("restricted_developers")
	private List<String> restrictedDevelopers;

	@JsonProperty("current_version")
	private String currentVersion;

	@JsonProperty("lock_order_on_error")
	private Boolean lockOrderOnError;

	@Override
	public String getGraphVersionCalculated() {
		return null;
	}
}