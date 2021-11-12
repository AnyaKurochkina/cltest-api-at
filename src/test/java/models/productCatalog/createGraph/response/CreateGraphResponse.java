package models.productCatalog.createGraph.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.Http;
import core.helper.JsonHelper;
import core.helper.JsonTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.subModels.Flavor;
import org.json.JSONObject;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGraphResponse{
	private JsonHelper jsonHelper = new JsonHelper();

	public JSONObject toJson() {
		return jsonHelper.getJsonTemplate("/productCatalog/graphs/createGraph.json").build();
	}

	public void createGraph(){
		String object = new Http("http://d4-product-catalog.apps.d0-oscp.corp.dev.vtb/")
				.setContentType("application/json")
				.setWithoutToken()
				.post("graphs/?save_as_next_version=true", toJson())
				.assertStatus(200)
				.toString();
	}

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

	@JsonProperty("ui_schema")
	private UiSchema uiSchema;
}