package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Collections;
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
    Integer checkFall;
    Integer checkInterval;
    Integer checkRise;
    String protocol;
    String checkName;
    List<CheckString> checkStrings;

    /* httpchk */
    String checkMethod;
    String checkUri;
    String versionAndHeaders;

    public static HealthCheckBuilder simpleTcpHealthCheck(String name, String backendName) {
        List<CheckString> checkStrings = Collections.singletonList(CheckString.builder()
                .stringPort(1234)
                .stringAddress("10.10.10.1")
                .stringUseSsl("disabled")
                .stringSendProxy("enabled")
                .build());
        return HealthCheck.builder()
                .checkName(name)
                .backendName(backendName)
                .checkFall(3)
                .checkInterval(5000)
                .checkRise(3)
                .protocol("tcp-check")
                .checkStrings(checkStrings);
    }

    public static HealthCheckBuilder simpleHttpHealthCheck(String name, String backendName) {
        List<CheckString> checkStrings = Collections.singletonList(CheckString.builder()
                .stringUseSsl("enabled")
                .stringSendProxy("disabled")
                .stringMethod("GET")
                .stringUri("/")
                .stringVersion("HTTP/1.1")
                .stringHostHdr("header")
                .stringValue("200")
                .stringMatch("status")
                .build());
        return HealthCheck.builder()
                .checkName(name)
                .backendName(backendName)
                .checkFall(2)
                .checkInterval(1000)
                .checkRise(2)
                .protocol("httpchk")
                .checkStrings(checkStrings);
    }
}
