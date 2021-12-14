package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.OrgDirection.createOrgDirection.response.CreateOrgDirectionResponse;
import httpModels.productCatalog.OrgDirection.createOrgDirection.response.ExtraData;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.OrgDirectionSteps;

import static core.helper.JsonHelper.convertResponseOnClass;

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

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/orgDirection/orgDirection.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", orgDirectionName)
                .set("$.description", description)
                .build();
    }

    @Override
    @Step("Создание направления")
    protected void create() {
        String response = new Http(Configure.ProductCatalogURL)
                
                .body(toJson())
                .post("org_direction/")
                .assertStatus(201)
                .toString();
        CreateOrgDirectionResponse createOrgDirectionResponse = convertResponseOnClass(response, CreateOrgDirectionResponse.class);
        orgDirectionId = createOrgDirectionResponse.getId();
        Assertions.assertNotNull(orgDirectionId, "Экшен с именем: " + orgDirectionName + ", не создался");
    }

    @Override
    @Step("Удаление направления")
    protected void delete() {
        OrgDirectionSteps steps = new OrgDirectionSteps();
        steps.deleteOrgDirectoryById(orgDirectionId);
    }
}
