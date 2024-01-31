package models.t1.cdn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResourceListItem {

    private String cname;
    private Object description;
    private Object rules;
    private Object enabled;
    private Object originProtocol;
    private Object proxySslEnabled;
    private String originGroupName;
    private Object vpEnabled;
    private Object shielded;
    private Boolean sslEnabled;
    private Object options;
    private Object canPurgeByUrls;
    private Object client;
    private Boolean sslAutomated;
    private String id;
    private Object shieldRoutingMap;
    private Object primaryResource;
    private Object secondaryHostnames;
    private Object shieldEnabled;
    private String created;
    private Object sslLeEnabled;
    private Object active;
    private Boolean presetApplied;
    private Object sslData;
    private Object suspended;
    private Object proxySslData;
    private Object originGroup;
    private Boolean deleted;
    private Object logTarget;
    private Object shieldDc;
    private Object name;
    private Object proxySslCa;
    private Object updated;
    private Object fullCustomEnabled;
    private String status;
}