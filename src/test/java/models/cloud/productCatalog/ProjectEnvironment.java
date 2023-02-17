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
public class ProjectEnvironment {

    @JsonProperty("environment_type")
    private List<String> environment_type;
    @JsonProperty("name")
    private List<String> name;

    public ProjectEnvironment(List<String> environmentType) {
        this.environment_type = environmentType;
    }
}