package models.cloud.subModels.loadBalancer;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Frontend {
    @Builder.Default
    String mode = "tcp";
    @Builder.Default
    Integer frontendPort = 443;
    @EqualsAndHashCode.Include
    String frontendName;
    String defaultBackendNameTcp;
}
