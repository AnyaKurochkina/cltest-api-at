package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gslb {
    @Builder.Default
    Boolean advancedOptions = false;
    Frontend frontend;
    @EqualsAndHashCode.Include
    String globalname;
    @Builder.Default
    HealthCheckParams healthCheckParams = HealthCheckParams.builder().build();

    public static class GslbBuilder {
        public GslbBuilder frontend(Frontend frontend) {
            if(Objects.nonNull(frontend.getDefaultBackendNameHttp()))
                frontend.setDefaultBackendName(frontend.getDefaultBackendNameHttp());
            if(Objects.nonNull(frontend.getDefaultBackendNameTcp()))
                frontend.setDefaultBackendName(frontend.getDefaultBackendNameTcp());
            frontend.setDefaultBackendNameHttp(null);
            frontend.setDefaultBackendNameTcp(null);
            this.frontend = frontend;
            return this;
        }
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HealthCheckParams {
        @Builder.Default
        Integer interval = 10;
        @Builder.Default
        Integer retries = 2;
        @Builder.Default
        Integer timeout = 5;
        String urlPath;
        Boolean useSsl;
    }
}
