package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gslb {
    String frontend;
    @EqualsAndHashCode.Include
    String globalname;
}
