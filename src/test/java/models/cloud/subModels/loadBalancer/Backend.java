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
    @Builder.Default
    Integer checkPort = 32344;
    @Builder.Default
    String advCheck = "tcp-check";//"tcp-check";
    @Builder.Default
    Integer checkFall = 3;
    @Builder.Default
    String checkSsl = "disabled";
    @Builder.Default
    String match = "string";
    @Builder.Default
    Integer checkRise = 3;
    @Builder.Default
    Integer checkInterval = 5000;
    String pattern;
    String data;

    /* httpchk GET*/
    String checkMethod;
    String checkUri;
    String versionAndHeaders;

    /* httpchk PUT*/
    String body;
}
