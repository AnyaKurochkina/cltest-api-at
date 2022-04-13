package models.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.jinja2.createJinja2.response.CreateJinjaResponse;
import httpModels.productCatalog.jinja2.createJinja2.response.Jinja2Data;
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
public class Jinja2 extends Entity {

    private String jinja2Template;
    private Jinja2Data jinja2Data;
    private String name;
    private String description;
    private String title;
    private String jsonTemplate;
    private String jinjaId;
    private final String productName = "jinja2_templates/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/jinja2/createJinja.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.jinja2Template", jinja2Template)
                .build();
    }

    @Override
    @Step("Создание jinja2")
    protected void create() {
        jinjaId = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateJinjaResponse.class)
                .getId();
        Assertions.assertNotNull(jinjaId, "Jinja с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление jinja2")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + jinjaId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate, Configure.ProductCatalogURL);
        Assertions.assertFalse(productCatalogSteps.isExists(name));
    }
}
