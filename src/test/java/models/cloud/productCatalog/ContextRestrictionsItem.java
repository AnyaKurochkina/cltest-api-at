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
public class ContextRestrictionsItem {

    private String organization;
    private ProjectEnvironment project_environment;
    private InformationSystem information_system;

    public ContextRestrictionsItem(ProjectEnvironment projectEnvironment) {
        this.project_environment = projectEnvironment;
    }

    public ContextRestrictionsItem(ProjectEnvironment projectEnvironment, String organization) {
        this.project_environment = projectEnvironment;
        this.organization = organization;
    }
}