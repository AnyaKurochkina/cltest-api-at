package models.t1.cdn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceListItem {

    @JsonProperty("cname")
    private String cname;

    @JsonProperty("description")
    private Object description;

    @JsonProperty("rules")
    private Object rules;

    @JsonProperty("enabled")
    private Object enabled;

    @JsonProperty("originProtocol")
    private Object originProtocol;

    @JsonProperty("proxy_ssl_enabled")
    private Object proxySslEnabled;

    @JsonProperty("originGroup_name")
    private String originGroupName;

    @JsonProperty("vp_enabled")
    private Object vpEnabled;

    @JsonProperty("shielded")
    private Object shielded;

    @JsonProperty("sslEnabled")
    private Boolean sslEnabled;

    @JsonProperty("options")
    private Object options;

    @JsonProperty("can_purge_by_urls")
    private Object canPurgeByUrls;

    @JsonProperty("client")
    private Object client;

    @JsonProperty("ssl_automated")
    private Boolean sslAutomated;

    @JsonProperty("id")
    private String id;

    @JsonProperty("shield_routing_map")
    private Object shieldRoutingMap;

    @JsonProperty("primary_resource")
    private Object primaryResource;

    @JsonProperty("secondaryHostnames")
    private Object secondaryHostnames;

    @JsonProperty("shield_enabled")
    private Object shieldEnabled;

    @JsonProperty("created")
    private String created;

    @JsonProperty("ssl_le_enabled")
    private Object sslLeEnabled;

    @JsonProperty("active")
    private Object active;

    @JsonProperty("preset_applied")
    private Boolean presetApplied;

    @JsonProperty("sslData")
    private Object sslData;

    @JsonProperty("suspended")
    private Object suspended;

    @JsonProperty("proxy_ssl_data")
    private Object proxySslData;

    @JsonProperty("originGroup")
    private Object originGroup;

    @JsonProperty("deleted")
    private Boolean deleted;

    @JsonProperty("logTarget")
    private Object logTarget;

    @JsonProperty("shield_dc")
    private Object shieldDc;

    @JsonProperty("name")
    private Object name;

    @JsonProperty("proxy_ssl_ca")
    private Object proxySslCa;

    @JsonProperty("updated")
    private Object updated;

    @JsonProperty("full_custom_enabled")
    private Object fullCustomEnabled;

    @JsonProperty("status")
    private String status;
}