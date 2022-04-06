package httpModels.productCatalog.itemVisualItem.getVisualTemplate;

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
public class FullTemplate{
    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private List<String> value;

}