package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
