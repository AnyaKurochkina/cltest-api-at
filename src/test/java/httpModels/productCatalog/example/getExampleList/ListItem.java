package httpModels.productCatalog.example.getExampleList;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ItemImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListItem implements ItemImpl {

	@JsonProperty("context_data")
	private ContextData contextData;

	@JsonProperty("form_data")
	private FormData formData;

	@JsonProperty("json_schema")
	private JsonSchema jsonSchema;

	@JsonProperty("update_dt")
	private String updateDt;

	@JsonProperty("name")
	private String name;

	@JsonProperty("create_dt")
	private String createDt;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("ui_schema")
	private UiSchema uiSchema;

	@Override
	public String getCreateData() {
		return createDt;
	}

	@Override
	public String getUpDateData() {
		return updateDt;
	}
}