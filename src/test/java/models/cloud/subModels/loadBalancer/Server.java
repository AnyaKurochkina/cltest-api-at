package models.cloud.subModels.loadBalancer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Server {
    Integer port;
    String name;
    String address;
    @Builder.Default
    String sendProxy = "disabled";
    String backup;
    Integer fall;
    Integer rise;
    Integer inter;
    String ssl;
}
