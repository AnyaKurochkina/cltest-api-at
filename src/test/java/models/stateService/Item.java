package models.stateService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedHashMap;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Item {
    private String id;
    private LinkedHashMap<String, Object> data;
    @JsonProperty("action_id")
    private String actionId;
    private String subtype;
    @JsonProperty("item_id")
    private String itemId;
    @JsonProperty("created_row_dt")
    private String createdRowDt;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("graph_id")
    private String graphId;
    private String type;
    @JsonProperty("order_id")
    private String orderId;
    private String status;
    private String jsonTemplate;
}