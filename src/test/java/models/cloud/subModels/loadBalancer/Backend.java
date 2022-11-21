package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Backend {
    @Builder.Default
    String mode = "tcp";
    List<Server> servers;
    @Builder.Default
    Boolean advancedCheck = false;
    @Builder.Default
    String balancingAlgorithm = "leastconn";
    @Builder.Default
    String advCheck = "httpchk";
    @Builder.Default
    Integer checkFall = 3;
    @Builder.Default
    Integer checkPort = 80;
    @Builder.Default
    Integer checkRise = 3;
    @Builder.Default
    Integer checkInterval = 5000;
    @Builder.Default
    String checkUri = "/";
    @Builder.Default
    String check_method = "GET";
    @EqualsAndHashCode.Include
    String backendName;
    String versionAndHeaders;
}
