package httpModels.productCatalog.OrgDirection.getOrgDirectionList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListItem{

	@JsonProperty("extra_data")
	private ExtraData extraData;

	@JsonProperty("name")
	private String name;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private String id;
}