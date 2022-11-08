package models.cloud.subModels.loadBalancer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Server {
    Integer port;
    String name;
    String address;
}
