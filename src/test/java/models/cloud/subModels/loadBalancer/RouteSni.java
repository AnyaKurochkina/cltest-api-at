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
    private String globalname;
    private List<Route> routes;

    @Data
    @AllArgsConstructor
    public static class Route {
        String backendName;
        String name;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RouteCheck {
        int index;
        List<String> aliases;
        String routeName;
        String backendName;
        String frontendName;
    }

    @Data
    @Builder
    public static class Alias {
        String name;
    }
}
