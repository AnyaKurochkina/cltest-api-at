package models.cloud.productCatalog.orgDirection;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import httpModels.productCatalog.orgDirection.getOrgDirection.response.ExtraData;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ServiceSteps;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.productCatalog.OrgDirectionSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrgDirection extends Entity implements IProductCatalog {
    @JsonProperty("extra_data")
    private ExtraData extraData;
    private String icon;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("icon_store_id")
    private String iconStoreId;
    private String name;
    private String description;
    private String id;
    private String title;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/orgDirection/orgDirection.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.icon_store_id", iconStoreId)
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .build();
    }

    @Override
    protected void create() {
        if (isOrgDirectionExists(name)) {
            List<String> serviceIdList = getServiceUsedOrgDirection(getOrgDirectionByName(name).getId()).jsonPath().getList("id");
            serviceIdList.forEach(ServiceSteps::deleteServiceById);
            deleteOrgDirectionById(getOrgDirectionByName(name).getId());
        }
        OrgDirection createOrgDirection = createOrgDirection(toJson())
                .assertStatus(201)
                .extractAs(OrgDirection.class);
        StringUtils.copyAvailableFields(createOrgDirection, this);
        Assertions.assertNotNull(id, "Направление с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteOrgDirectionById(id);
        assertFalse(isOrgDirectionExists(name));
    }
}
