package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.orgDirection.createOrgDirection.response.CreateOrgDirectionResponse;
import httpModels.productCatalog.orgDirection.createOrgDirection.response.ExtraData;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

@Log4j2
@Builder
@Getter
public class OrgDirection extends Entity {
    private ExtraData extraData;
    private String orgDirectionName;
    private String icon;
    private String description;
    private String orgDirectionId;
    private String jsonTemplate;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("org_direction/", "productCatalog/orgDirection/orgDirection.json");

    private final String productName = "org_direction/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/orgDirection/orgDirection.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", orgDirectionName)
                .set("$.description", description)
                .build();
    }

    @Override
    @Step("Создание направления")
    protected void create() {
        CreateOrgDirectionResponse createOrgDirectionResponse = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post("org_direction/")
                .assertStatus(201)
                .extractAs(CreateOrgDirectionResponse.class);
        orgDirectionId = createOrgDirectionResponse.getId();
        Assertions.assertNotNull(orgDirectionId, "Направление с именем: " + orgDirectionName + ", не создался");
    }

    @Override
    @Step("Удаление направления")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + orgDirectionId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(orgDirectionName));
    }
}
