package httpModels.productCatalog.jinja2.createJinja2.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJinjaResponse{

	@JsonProperty("jinja2_template")
	private String jinja2Template;

	@JsonProperty("jinja2_data")
	private Jinja2Data jinja2Data;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;
}