package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mifmif.common.regex.Generex;
import lombok.*;

import java.util.List;
import java.util.Random;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Frontend {
    String mode;
    Integer frontendPort;
    @EqualsAndHashCode.Include
    String frontendName;

    Boolean keepAliveTcp;
    Integer keepCntTcp;
    Integer keepTimerTcp;

    // only http/https
    Boolean keepAliveHttp;
    Integer keepTimerHttp;
    String defaultBackendNameHttp;

    // only tcp
    String defaultBackendNameTcp;

    //only deserialize
    List<Object> aliases;
    String sslCertificate;
    String defaultBackendName;


    public static FrontendBuilder simpleTcpFrontend(String backendName) {
        return Frontend.builder()
                .mode("tcp")
                .defaultBackendNameTcp(backendName)
                .frontendName(new Generex("at-frontend-[a-z]{4}").random())
                .frontendPort(12000 + new Random().nextInt(1001))
                .keepAliveTcp(false);
    }

    public static FrontendBuilder simpleHttpFrontend(String backendName) {
        return Frontend.builder()
                .mode("http")
                .defaultBackendNameHttp(backendName)
                .frontendName(new Generex("at-frontend-[a-z]{4}").random())
                .frontendPort(12000 + new Random().nextInt(1001))
                .keepAliveTcp(false)
                .keepAliveHttp(true)
                .keepTimerHttp(20000);
    }
}
