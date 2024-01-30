package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mifmif.common.regex.Generex;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Backend {
    String mode;
    List<Server> servers;
    String balancingAlgorithm;
    @EqualsAndHashCode.Include
    String backendName;
    Integer checkInterval;
    Integer checkFall;
    Integer checkRise;
    String advCheck;

    /* tcp-check */

    /* httpchk */
    String checkUri;
    String stringHostHdr;
    String stringVersion;
    String stringMatch;
    String stringValue;

    /* http mode */
    String httpReuse;
    Boolean cookieStatus;
    Boolean keepAlive;
    //if keepAlive:
    Boolean keepHttpDeactivate;
    Integer keepTimer;

    public static BackendBuilder simpleTcpBackendWidthTcpCheck() {
        List<Server> servers = Collections.singletonList(Server.simpleTcpServer().build());
        return Backend.builder()
                .mode("tcp")
                .servers(servers)
                .balancingAlgorithm("leastconn")
                .backendName(new Generex("at-backend-[a-z]{5}").random())
                .checkInterval(5000)
                .checkFall(3)
                .checkRise(3)
                .advCheck("tcp-check");
    }

    public static BackendBuilder simpleHttpBackendWidthHttpCheck() {
        List<Server> servers = Collections.singletonList(Server.simpleHttpServer().build());
        return Backend.builder()
                .mode("http")
                .servers(servers)
                .balancingAlgorithm("leastconn")
                .backendName(new Generex("at-backend-[a-z]{5}").random())
                .checkInterval(5000)
                .checkFall(3)
                .checkRise(3)
                .advCheck("httpchk")
                .checkUri("/")
                .stringVersion("HTTP/1.1")
                .stringMatch("status")
                .stringValue("200")
                .cookieStatus(false)
                .keepAlive(false)
                .httpReuse("never");
    }
}
