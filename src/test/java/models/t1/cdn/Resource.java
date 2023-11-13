package models.t1.cdn;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Resource {
    private String domainName;
    private String hostName;
}
