package httpModels.productCatalog.service.getService.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraData{

	@JsonProperty("auto_open_form")
	private Boolean autoOpenForm;

	@JsonProperty("inventory_actions")
	private List<InventoryActionsItem> inventoryActions;

	@JsonProperty("turn_off_inventory")
	private Boolean turnOffInventory;
}