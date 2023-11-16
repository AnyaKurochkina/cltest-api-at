package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Frontend {
    @Builder.Default
    String mode = "tcp";
    @Builder.Default
    Integer frontendPort = 443;
    @EqualsAndHashCode.Include
    String frontendName;
    String defaultBackendNameTcp;
    String defaultBackendNameHttp;

    String defaultBackendName;
}
