package httpModels.productCatalog.productOrgInfoSystem.createInfoSystem;

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
public class CreateInfoSystemResponse implements GetImpl {

	@JsonProperty("product")
	private String product;

	@JsonProperty("id")
	private String id;

	@JsonProperty("organization")
	private String organization;

	@JsonProperty("update_dt")
	private String update_dt;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("information_systems")
	private List<String> informationSystems;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public String getGraphVersionCalculated() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getId() {
		return id;
	}
}