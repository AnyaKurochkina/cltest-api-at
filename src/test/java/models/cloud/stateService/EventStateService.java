package models.cloud.stateService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventStateService{

	@JsonProperty("data")
	private Object data;

	@JsonProperty("action_id")
	private String actionId;

	@JsonProperty("subtype")
	private String subtype;

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("created_row_dt")
	private String createdRowDt;

	@JsonProperty("create_dt")
	private String createDt;

	@JsonProperty("update_data")
	private Object updateData;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("type")
	private String type;

	@JsonProperty("order_id")
	private String orderId;

	@JsonProperty("status")
	private String status;
}