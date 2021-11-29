package httpModels.productCatalog.getService.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSource{

	@JsonProperty("inventory_tags")
	private InventoryTags inventoryTags;

	@JsonProperty("test")
	private Boolean test;
}