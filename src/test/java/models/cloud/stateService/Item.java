package models.cloud.stateService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.cloud.productCatalog.action.Action;

import java.util.LinkedHashMap;
import java.util.List;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Item {
    private String id;
    private LinkedHashMap<String, Object> data;
    private LinkedHashMap<String, Object> parent_item;
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
    @JsonProperty("actions")
    private List<Action> actions;
    @JsonProperty("graph_id")
    private String graphId;
    @JsonProperty("provider")
    private String provider;
    private String type;
    @JsonProperty("order_id")
    private String orderId;
    private String status;
    private String jsonTemplate;
}