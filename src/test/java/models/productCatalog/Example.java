package models.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.example.createExample.*;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

@Log4j2
@Builder
@Getter
public class Example extends Entity {
    private ContextData contextData;
    private FormData formData;
    private JsonSchema jsonSchema;
    private String updateDt;
    private String name;
    private String createDt;
    private String description;
    private String id;
    private String title;
    private UiSchema uiSchema;
    private String jsonTemplate;
    private final String productName = "example/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/examples/createExample.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .build();
    }

    @Override
    @Step("Создание примера")
    protected void create() {
        id = new Http(Configure.ProductCatalogURL + "/api/v1/")
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateExampleResponse.class)
                .getId();
        Assertions.assertNotNull(id, "Пример с именем: " + name + ", не создался");

    }

    @Override
    @Step("Удаление примера")
    protected void delete() {
        new Http(Configure.ProductCatalogURL + "/api/v1/")
                .delete(productName + id + "/")
                .assertStatus(204);
    }
}
