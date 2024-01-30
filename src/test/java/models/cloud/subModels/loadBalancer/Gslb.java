package models.cloud.subModels.loadBalancer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mifmif.common.regex.Generex;
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

    public static GslbBuilder simpleGslb(String frontend) {
        return Gslb.builder()
                .globalname(new Generex("gslb-tcp-[0-9]{10}").random())
                .frontend(frontend);
    }
}
