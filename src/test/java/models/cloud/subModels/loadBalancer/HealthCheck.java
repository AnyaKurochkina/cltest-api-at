package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HealthCheck {
    String backendName;
    @Builder.Default
    Integer checkFall = 3;
    @Builder.Default
    Integer checkInterval = 5000;
    @Builder.Default
    Integer checkRise = 3;
    @Builder.Default
    String protocol = "tcp-check";

    List<CheckString> checkStrings;

    /* httpchk */
    String checkMethod;
    String checkUri;
    String versionAndHeaders;
}
