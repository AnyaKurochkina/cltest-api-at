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
    String backendName;

    /* tcp-check*/
    Integer checkPort;
    String advCheck;
    Integer checkFall;
    String checkSsl;
    String match;
    Integer checkRise;
    Integer checkInterval;
    String pattern;
    String data;

    /* httpchk GET*/
    String checkMethod;
    String checkUri;
    String versionAndHeaders;

    /* httpchk PUT*/
    String body;
}
