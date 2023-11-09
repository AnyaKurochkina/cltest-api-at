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
    @Builder.Default
    String sendProxy = "disabled";
    String backup;
    Integer fall;
    Integer rise;
    Integer inter;
    String useSsl;
    Integer slowStart;

    String ssl;
}
