package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CheckString {
    String stringSendProxy;
    String stringUseSsl;

    //only for http
    String stringHostHdr;
    String stringMatch;
    String stringMethod;
    String stringUri;
    String stringValue;
    String stringVersion;

    //only for tcp
    String stringAddress;
    Integer stringPort;
}
