package httpModels.productCatalog.Service.getService.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryActionsItem{

	@JsonProperty("enable")
	private Boolean enable;

	@JsonProperty("name")
	private String name;
}