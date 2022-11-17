package models.cloud.productCatalog.orgDirection;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import httpModels.productCatalog.orgDirection.getOrgDirection.response.ExtraData;
import httpModels.productCatalog.orgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import static core.helper.Configure.ProductCatalogURL;

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
    private String jsonTemplate;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("/org_direction/",
            "productCatalog/orgDirection/orgDirection.json");

    private final String productName = "/api/v1/org_direction/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/orgDirection/orgDirection.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.icon_store_id", iconStoreId)
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .build();
    }

    @Override
    @Step("Создание направления")
    protected void create() {
        if (productCatalogSteps.isExists(name)) {
            productCatalogSteps.deleteByName(name, GetOrgDirectionListResponse.class);
        }
        OrgDirection createOrgDirection = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(OrgDirection.class);
        StringUtils.copyAvailableFields(createOrgDirection, this);
        Assertions.assertNotNull(id, "Направление с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление направления")
    protected void delete() {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + id + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(name));
    }
}
