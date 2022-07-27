package models.stateService;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;

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
    @JsonProperty("update_data")
    private LinkedHashMap<String, Object> updateData;
    @JsonProperty("graph_id")
    private String graphId;
    private String type;
    @JsonProperty("order_id")
    private String orderId;
    private String status;
    private String jsonTemplate;
}