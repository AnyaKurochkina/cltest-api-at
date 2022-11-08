package models.cloud.subModels.loadBalancer;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Gslb {
    @Builder.Default
    Boolean advancedOptions = false;
    Frontend frontend;
    @EqualsAndHashCode.Include
    String globalname;
    @Builder.Default
    HealthCheckParams healthCheckParams = HealthCheckParams.builder().build();

    @Data
    @Builder
    public static class HealthCheckParams {
        @Builder.Default
        Integer interval = 10;
        @Builder.Default
        Integer retries = 2;
        @Builder.Default
        Integer timeout = 5;
    }
}
