package models.productCatalog;

import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.template.createTemplate.response.CreateTemplateResponse;
import httpModels.productCatalog.template.createTemplate.response.Input;
import httpModels.productCatalog.template.createTemplate.response.Output;
import httpModels.productCatalog.template.createTemplate.response.PrintedOutput;
import httpModels.productCatalog.template.getTemplate.response.GetTemplateResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter

public class Template extends Entity {

    private Boolean additionalInput;
    private String color;
    private String icon;
    private String description;
    private String run;
    private Boolean priorityCanBeOverridden;
    private Boolean logCanBeOverridden;
    private Integer timeout;
    private Double coordsX;
    private Output output;
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
    private Input input;
    private Object extraData;
    private String templateName;
    private List<Object> allowedGroups;
    private Boolean additionalOutput;
    private String jsonTemplate;
    private String version;
    private String type;
    private String title;

    private final String productName = "templates/";

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
                .build();
    }

    @Override
    protected void create() {
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate,
                ProductCatalogURL + "/api/v1/");
        if (productCatalogSteps.isExists(templateName)) {
            productCatalogSteps.deleteByName(templateName, GetTemplateResponse.class);
        }
        CreateTemplateResponse createTemplateResponse = new Http(ProductCatalogURL + "/api/v1/")
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateTemplateResponse.class);
        templateId = createTemplateResponse.getId();
        Assertions.assertNotNull(templateId, "Шаблон с именем: " + templateName + ", не создался");
    }

    @Override
    protected void delete() {
         new Http(ProductCatalogURL + "/api/v1/")
                .delete(productName + templateId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate,
                ProductCatalogURL + "/api/v1/");
        Assertions.assertFalse(productCatalogSteps.isExists(templateName));
    }
}
