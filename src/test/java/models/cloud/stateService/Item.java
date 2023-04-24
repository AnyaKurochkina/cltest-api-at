package models.cloud.stateService;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;

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
    @JsonProperty("external_provider_id")
    private String externalProviderId;
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
    private String folder;
    @JsonProperty("maintance_mode")
    private Boolean maintanceMode;
    @JsonProperty("update_data")
    private Object updateData;
    @JsonProperty("children_list")
    private List<Object> childrenList;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("stateService/createItem.json")
                .set("$.order_id", orderId)
                .set("$.action_id", actionId)
                .set("$.graph_id", graphId)
                .set("$.type", type)
                .setIfNullRemove("$.subtype", subtype)
                .setIfNullRemove("$.status", status)
                .set("$.data", data)
                .set("$.item_id", itemId)
                .set("$.update_data", updateData)
                .build();
    }

}