package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CheckString {
    String stringAddress;
    String stringBody;
    String stringData;
    String stringHostHdr;
    String stringMatch;
    String stringMethod;
    String stringSendProxy;
    String stringType;
    String stringUri;
    String stringUseSsl;
    String stringValue;
    String stringVersion;
    Integer stringPort;
}
