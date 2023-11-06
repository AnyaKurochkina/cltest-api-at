package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RouteSni {
    public DnsPrefix dnsPrefix;
    public List<Route> routes;

    @Data
    @NoArgsConstructor
    public static class DnsPrefix {
        int ttl;
        List<Member> members;
        String monitor;
        String fallback;
        String lbMethod;
        String globalname;
        MonitorParams monitorParams;

        @Data
        @NoArgsConstructor
        public static class Member {
            String ip;
            String name;
            int weight;
            boolean maintenanceMode;
        }

        @Data
        @NoArgsConstructor
        public static class MonitorParams {
            int port;
            int retries;
            int timeout;
            boolean useSsl;
            int interval;
            String backendName;
        }
    }

    @Data
    public static class Route {
        String backendName;
        String name;

        public Route(String backendName, String name) {
            this.backendName = backendName;
            this.name = name;
        }
    }

    @Data
    public static class RouteCheck {
        int index;
        String routeName;
        String backendName;
        String frontendName;
    }
}
