package models.t1.portalBack;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.List;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class VmWareOrganization extends Entity {
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    @JsonProperty("updated_at")
    private String updatedAt;
    private List<RolesItem> roles;
    @JsonProperty("vcloud_url")
    private String vcloudUrl;
    private String name;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("vcloud_uuid")
    private String vcloudUuid;
    private String id;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("availability_zone_name")
    private String availabilityZoneName;
    @JsonProperty("edge_gateway_type")
    private String edgeGatewayType;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("/t1/createOrg.json")
                .set("$.vdc_organization.name", name)
                .set("$.vdc_organization.project_name", projectName)
                .set("$.vdc_organization.availability_zone_name", availabilityZoneName)
                .build();
    }

    @Override
    protected void create() {

    }

    @Override
    protected void delete() {

    }
}
