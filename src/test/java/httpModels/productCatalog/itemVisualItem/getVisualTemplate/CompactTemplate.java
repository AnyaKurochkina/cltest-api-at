package httpModels.productCatalog.itemVisualItem.getVisualTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.Name;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.Status;
import httpModels.productCatalog.itemVisualItem.createVisualTemplate.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompactTemplate{

    @JsonProperty("name")
    private Name name;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("status")
    private Status status;
}