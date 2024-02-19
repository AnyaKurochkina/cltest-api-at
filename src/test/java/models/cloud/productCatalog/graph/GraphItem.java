package models.cloud.productCatalog.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static core.helper.JsonHelper.getStringFromFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphItem {

    @JsonProperty("is_sequential")
    private Boolean isSequential;
    @JsonProperty("hold")
    private Boolean hold;
    @JsonProperty("color")
    private String color;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("for_each")
    private Object forEach;
    @JsonProperty("description")
    private String description;
    @JsonProperty("timeout")
    private Integer timeout;
    @JsonProperty("output")
    private Map<String, Object> output;
    @JsonProperty("coords_x")
    private Integer coordsX;
    @JsonProperty("number")
    private Integer number;
    @JsonProperty("printed_output")
    private Object printedOutput;
    @JsonProperty("coords_y")
    private Integer coordsY;
    @JsonProperty("depends")
    private List<Object> depends;
    @JsonProperty("count")
    private String count;
    @JsonProperty("log_level")
    private String logLevel;
    @JsonProperty("damage_order_on_error")
    private Boolean damageOrderOnError;
    @JsonProperty("priority")
    private Object priority;
    @JsonProperty("on_prebilling")
    private Boolean onPrebilling;
    @JsonProperty("input")
    private Map<String, String> input;
    @JsonProperty("not_damage_on_error")
    private Boolean notDamageOnError;
    @JsonProperty("condition")
    private String condition;
    @JsonProperty("extra_data")
    private Object extraData;
    @JsonProperty("name")
    private String name;
    @JsonProperty("source_type")
    private String sourceType;
    @JsonProperty("source_id")
    private String sourceId;
    @JsonProperty("source_version")
    private String sourceVersion;
    @JsonProperty("source_version_calculated")
    private String sourceVersionCalculated;
    @JsonProperty("source_version_pattern")
    private String sourceVersionPattern;
    @JsonProperty("run_on_rollback")
    private Boolean runOnRollback;
    @JsonProperty("lock_order_on_error")
    private Boolean lockOrderOnError;
    @JsonProperty("icon_store_id")
    private Object iconStoreId;
    private String template;
    @JsonProperty("current_version")
    private String currentVersion;
    private String title;
    private String type;

    public static GraphItem getGraphItemFromJsonTemplate() {
        return JsonHelper.deserialize(getStringFromFile("/productCatalog/graphs/GraphItem.json"), GraphItem.class);
    }

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/graphs/GraphItem.json")
                .set("$.is_sequential", isSequential)
                .set("$.hold", hold)
                .set("$.color", color)
                .set("$.icon_url", iconUrl)
                .set("$.for_each", forEach)
                .set("$.description", description)
                .set("$.timeout", timeout)
                .set("$.output", output)
                .set("$.printed_output", printedOutput)
                .set("$.coords_x", coordsX)
                .set("$.depends", depends)
                .set("$.count", count)
                .set("$.log_level", logLevel)
                .set("$.damage_order_on_error", damageOrderOnError)
                .set("$.priority", priority)
                .set("$.on_prebilling", onPrebilling)
                .set("$.input", input)
                .set("$.not_damage_on_error", notDamageOnError)
                .set("$.condition", condition)
                .set("$.extra_data", extraData)
                .set("$.name", name)
                .set("$.run_on_rollback", runOnRollback)
                .set("$.lock_order_on_error", lockOrderOnError)
                .set("$.icon_store_id", iconStoreId)
                .set("$.source_id", sourceId)
                .set("$.source_type", sourceType)
                .set("$.source_version", sourceVersion)
                .set("$.source_version_calculated", sourceVersionCalculated)
                .set("$.source_version_pattern", sourceVersionPattern)
                .build();
    }
}