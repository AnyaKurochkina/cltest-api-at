package httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ItemImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListItem implements ItemImpl {

	@JsonProperty("product")
	private String product;

	@JsonProperty("update_dt")
	private String updateDt;

	@JsonProperty("organization")
	private String organization;

	@JsonProperty("information_systems")
	private List<String> informationSystems;

	@JsonProperty("create_dt")
	private String createDt;

	@JsonProperty("id")
	private String id;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getCreateData() {
		return createDt;
	}

	@Override
	public String getUpDateData() {
		return updateDt;
	}
}