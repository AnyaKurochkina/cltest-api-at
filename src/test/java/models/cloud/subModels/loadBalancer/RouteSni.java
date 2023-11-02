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

        public DnsPrefix(Map dns) {
            this.ttl = (int)dns.get("ttl");
            this.members = (List)dns.get("members");
            this.monitor = (String)dns.get("monitor");
            this.fallback = (String)dns.get("fallback");
            this.lbMethod = (String)dns.get("lb_method");
            this.globalname = (String)dns.get("globalname");
            this.monitorParams = new MonitorParams((Map)dns.get("monitor_params"));
        }

        @Data
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

            public MonitorParams(Map params) {
                this.port = (int)params.get("port");
                this.retries = (int)params.get("retries");
                this.timeout = (int)params.get("timeout");
                this.interval = (int)params.get("interval");
                this.useSsl = (boolean) params.get("use_ssl");
                this.backendName = (String) params.get("backend_name");
            }
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
