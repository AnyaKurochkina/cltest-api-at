package models.cloud.productCatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContextRestrictionsItem {

    private String organization;
    @JsonProperty("project_environment")
    private ProjectEnvironment project_environment;
    @JsonProperty("information_system")
    private InformationSystem informationSystem;

    public ContextRestrictionsItem(ProjectEnvironment projectEnvironment) {
        this.project_environment = projectEnvironment;
    }

    public ContextRestrictionsItem(ProjectEnvironment projectEnvironment, String organization) {
        this.project_environment = projectEnvironment;
        this.organization = organization;
    }
}