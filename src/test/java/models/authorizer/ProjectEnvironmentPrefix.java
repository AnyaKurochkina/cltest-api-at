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
//        String informationSystemId = ((InformationSystem) InformationSystem.builder().build().createObject()).getId();
//        ProjectEnvironmentPrefix prefix = PortalBackSteps.getProjectEnvironmentPrefixByEnv(env, informationSystemId);
//        this.envType = prefix.getEnvType();
//        this.risName = prefix.getRisName();
//        this.id = prefix.getId();
//        this.projectEnvironmentId = prefix.getProjectEnvironmentId();
//        this.description = prefix.getDescription();
        this.env = env;
    }
}
