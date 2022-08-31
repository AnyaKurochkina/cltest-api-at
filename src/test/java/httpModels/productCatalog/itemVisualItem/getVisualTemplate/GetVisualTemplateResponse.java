package httpModels.productCatalog.itemVisualItem.getVisualTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetVisualTemplateResponse implements GetImpl {

    @JsonProperty("event_type")
    private List<String> eventType;

    @JsonProperty("compact_template")
    private CompactTemplate compactTemplate;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("full_template")
    private FullTemplate fullTemplate;

    @JsonProperty("event_provider")
    private List<String> eventProvider;

    @JsonProperty("name")
    private String name;

    @JsonProperty("default_item")
    private LinkedHashMap<String, Object> defaultItem;

    @JsonProperty("description")
    private String description;

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("create_dt")
    private String create_dt;

    @JsonProperty("update_dt")
    private String update_dt;

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getGraphVersionCalculated() {
        return null;
    }
}