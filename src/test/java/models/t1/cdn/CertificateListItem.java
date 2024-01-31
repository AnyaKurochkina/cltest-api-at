package models.t1.cdn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificateListItem {

    private Boolean automated;
    private Object certSubjectAlt;
    private Object hasRelatedResources;
    private Boolean deleted;
    private Object validityNotBefore;
    private Object certSubjectCn;
    private Object certIssuer;
    private String name;
    private Object sslCertificateChain;
    private String id;
    private Object validityNotAfter;
}
