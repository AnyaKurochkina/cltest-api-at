package models.authorizer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class ProjectEnvironmentPrefix  {
    String id;
    String envType;
    String env;
    String projectEnvironmentId;
    String risName;
    String description;

    public ProjectEnvironmentPrefix(String env){
        this.env = env;
    }
}
