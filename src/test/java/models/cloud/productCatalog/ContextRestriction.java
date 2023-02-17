package models.cloud.productCatalog;

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
public class ContextRestriction {

    @JsonProperty("context_restrictions")
    private List<ContextRestrictionsItem> contextRestrictions;
}