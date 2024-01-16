package models.cloud.stateService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ActionStateService {

    private Object data;

    @JsonProperty("action_id")
    private String actionId;

    private String subtype;

    @JsonProperty("created_row_dt")
    private String createdRowDt;

    @JsonProperty("create_dt")
    private String createDt;

    @JsonProperty("graph_version")
    private String graphVersion;

    @JsonProperty("graph_id")
    private String graphId;

    private String type;

    @JsonProperty("order_id")
    private String orderId;

    private String status;

    @JsonProperty("execution_log")
    private Object executionLog;
}