package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChangePublicationsMaintenanceMode {
    @Builder.Default
    Boolean accept = true;
    @Builder.Default
    Boolean forced = false;
    @JsonProperty("hostname-on")
    List<String> hostnameOn;
    @JsonProperty("hostname-off")
    List<String> hostnameOff;
    String state;
    @Builder.Default
    Integer stopDelay = 180000;
}
