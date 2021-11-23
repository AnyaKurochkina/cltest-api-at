package httpModels.productCatalog.getService.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtraData{

	@JsonProperty("inventory_actions")
	private List<InventoryActionsItem> inventoryActions;
}