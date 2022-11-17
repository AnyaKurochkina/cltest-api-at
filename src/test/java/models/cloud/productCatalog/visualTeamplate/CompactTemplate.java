package models.cloud.productCatalog.visualTeamplate;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String name;

    @JsonProperty("type")
    private String  type;

    @JsonProperty("status")
    private String status;
}
