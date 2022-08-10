package models.productCatalog;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.template.createTemplate.response.CreateTemplateResponse;
import httpModels.productCatalog.template.createTemplate.response.PrintedOutput;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;
import java.util.Map;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter

public class Template extends Entity {

    private Boolean additionalInput;
    private String color;
    private String icon;
    private String iconUrl;
    private String iconStoreId;
    private String description;
    private String run;
    private Boolean priorityCanBeOverridden;
    private Boolean logCanBeOverridden;
    private Integer timeout;
    private Double coordsX;
    private Map<String,Map<String,String>> output;
    private PrintedOutput printedOutput;
    private Boolean printedOutputCanBeOverridden;
    private List<Object> restrictedPaths;
    private Integer templateId;
    private Double coordsY;
    private Object rollback;
    private List<Object> allowedPaths;
    private String logLevel;
    private List<Object> restrictedGroups;
    private Integer priority;
    private Map<String,Map<String,String>> input;
    private Object extraData;
    private String templateName;
    private List<Object> allowedGroups;
    private Boolean additionalOutput;
    private String jsonTemplate;
    private String version;
    private String type;
    private String title;

    public static final String productName = "/api/v1/templates/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/templates/createTemplate.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", templateName)
                .set("$.version", version)
                .set("$.type", type)
                .set("$.title", title)
                .set("$.run", run)
                .set("$.input", input)
                .set("$.output", output)
                .set("$.timeout", timeout)
                .set("$.icon", icon)
                .set("$.icon_url", iconUrl)
                .set("$.icon_store_id", iconStoreId)
                .build();
    }

    @Override
    @Step("Создание шаблона")
    protected void create() {
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        if (productCatalogSteps.isExists(templateName)) {
            productCatalogSteps.deleteByName(templateName, GetTemplateListResponse.class);
        }
        CreateTemplateResponse createTemplateResponse = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateTemplateResponse.class);
        templateId = createTemplateResponse.getId();
        Assertions.assertNotNull(templateId, "Шаблон с именем: " + templateName + ", не создался");
    }

    @Override
    protected void delete() {
         new Http(ProductCatalogURL)
                 .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + templateId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(templateName));
    }
}
