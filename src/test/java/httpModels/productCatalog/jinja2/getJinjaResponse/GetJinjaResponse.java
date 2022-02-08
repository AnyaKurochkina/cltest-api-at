package httpModels.productCatalog.jinja2.getJinjaResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetJinjaResponse implements GetImpl {

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

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public String getGraphVersionCalculated() {
		return null;
	}
}