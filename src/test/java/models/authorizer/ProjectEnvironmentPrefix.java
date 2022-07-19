package models.authorizer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import steps.portalBack.PortalBackSteps;

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

    public static ProjectEnvironmentPrefix byType(String type){
        ProjectEnvironmentPrefix prefix = new ProjectEnvironmentPrefix();
        prefix.setEnvType(type.toUpperCase());
        return prefix;
    }

}
