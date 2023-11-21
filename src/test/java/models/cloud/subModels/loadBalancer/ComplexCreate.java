package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ComplexCreate {
    @Singular
    List<Backend> backends;
    @Singular
    List<Frontend> frontends;
    @Singular("addGslb")
    List<Gslb> gslb;
    @Singular
    List<HealthCheck> healthChecks;
    @Singular
    List<RouteSni> sniRoutes;
}
