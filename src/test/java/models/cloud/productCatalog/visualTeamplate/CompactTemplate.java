package models.cloud.productCatalog.visualTeamplate;

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
public class CompactTemplate {
    @JsonProperty("name")
    private Name name;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("status")
    private Status status;
    private List<Object> additional;
    private Object params;
    @JsonProperty("Endpoint")
    private String endpoint;
}
