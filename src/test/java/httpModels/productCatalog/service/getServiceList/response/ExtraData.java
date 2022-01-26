package httpModels.productCatalog.service.getServiceList.response;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
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
@JsonIgnoreType
public class ExtraData{

	@JsonProperty("service_info")
	private String serviceInfo;

	@JsonProperty("start_btn_label")
	private String startBtnLabel;

	@JsonProperty("printable_output")
	private List<PrintableOutputItem> printableOutput;

	@JsonProperty("test")
	private String test;

	@JsonProperty("auto_open_form")
	private Boolean autoOpenForm;

	@JsonProperty("inventory_actions")
	private List<InventoryActionsItem> inventoryActions;

	@JsonProperty("turn_off_inventory")
	private Boolean turnOffInventory;

	@JsonProperty("inventory_requirement")
	private String inventoryRequirement;

	@JsonProperty("access_groups")
	private List<String> accessGroups;
}