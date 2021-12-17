package httpModels.productCatalog.Graphs.patchGraph.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchGraphResponse{

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
}