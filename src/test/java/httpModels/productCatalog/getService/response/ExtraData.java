package httpModels.productCatalog.getService.response;

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

	@JsonProperty("test")
	private String test;

	@JsonProperty("printable_output")
	private List<PrintableOutputItem> printableOutput;

	@JsonProperty("auto_open_form")
	private Boolean autoOpenForm;

	@JsonProperty("inventory_actions")
	private List<InventoryActionsItem> inventoryActions;

	@JsonProperty("turn_off_inventory")
	private Boolean turnOffInventory;

	@JsonProperty("start_btn_label")
	private String startBtnLabel;

	@JsonProperty("inventory_requirement")
	private String inventoryRequirement;

	@JsonProperty("access_groups")
	private List<String> accessGroups;
}