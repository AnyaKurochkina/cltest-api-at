package models.cloud.productCatalog.visualTeamplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompactTemplate {
    @JsonProperty("name")
    private Name name;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("status")
    private Status status;
    private List<Object> additional;
    private Object params;
    @JsonProperty("Endpoint")
    private String endpoint;
    @JsonProperty("system")
    private Object system;
    private Object bootable;
    private Object size;
    private Object domain;
    private Object description;
    private Object networks;
    @JsonProperty("mac_address")
    private Object macAddress;
    private Object ip;
    private Object image;
    @JsonProperty("mTLS Endpoint")
    private Object mTLSEndpoint;
    private Object link;
    @JsonProperty("created_at")
    private Object createdAt;
}
