package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Server {
    Integer port;
    String name;
    String address;
    String sendProxy;
    Integer slowstart;
    String backup;

    //default only http "disabled"
    String useSsl;

    public static ServerBuilder simpleTcpServer() {
        return Server.builder()
                .name("d5soul-ngc004lk.corp.dev.vtb")
                .port(80)
                .address("10.226.48.194")
                .sendProxy("disabled")
                .slowstart(0)
                .backup("disabled");
    }

    public static ServerBuilder simpleHttpServer() {
        return Server.builder()
                .name("d5soul-ngc004lk.corp.dev.vtb")
                .port(443)
                .address("10.226.48.194")
                .sendProxy("disabled")
                .useSsl("disabled")
                .slowstart(0)
                .backup("disabled");
    }
}
