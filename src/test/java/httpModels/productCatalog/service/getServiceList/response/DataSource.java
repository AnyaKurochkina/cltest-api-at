package httpModels.productCatalog.service.getServiceList.response;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreType
public class DataSource{

	@JsonProperty("inventory_tags")
	private InventoryTags inventoryTags;

	@JsonProperty("test")
	private Boolean test;
}