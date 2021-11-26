package httpModels.productCatalog.OrgDirection.getOrgDirection.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrgDirectionResponse {

	@JsonProperty("extra_data")
	private ExtraData extraData;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private String id;
}