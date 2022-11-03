package models.cloud.productCatalog.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.example.createExample.CreateExampleResponse;
import httpModels.productCatalog.example.getExampleList.GetExampleListResponse;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.LinkedHashMap;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"jsonTemplate", "productName"}, callSuper = false)
@ToString(exclude = {"jsonTemplate", "productName"})
public class Example extends Entity {
    @Builder.Default
    @JsonProperty("context_data")
    private LinkedHashMap<Object, Object> contextData = new LinkedHashMap<>();
    @Builder.Default
    @JsonProperty("form_data")
    private LinkedHashMap<Object, Object> formData = new LinkedHashMap<>();
    @Builder.Default
    @JsonProperty("json_schema")
    private LinkedHashMap<Object, Object> jsonSchema = new LinkedHashMap<>();
    @JsonProperty("update_dt")
    private String updateDt;
    private String name;
    @JsonProperty("create_dt")
    private String createDt;
    private String description;
    private String id;
    private String title;
    @Builder.Default
    @JsonProperty("ui_schema")
    private LinkedHashMap<Object, Object> uiSchema = new LinkedHashMap<>();
    private String jsonTemplate;
    private String productName;

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/examples/createExample.json";
        productName = "/api/v1/example/";
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
                .set("$.ui_schema", uiSchema)
                .set("$.context_data", contextData)
                .set("$.form_data", formData)
                .set("$.json_schema", jsonSchema)
                .build();
    }

    @Override
    @Step("Создание примера")
    protected void create() {
        ProductCatalogSteps steps = new ProductCatalogSteps(productName, jsonTemplate);
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetExampleListResponse.class);
        }
        CreateExampleResponse example = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateExampleResponse.class);
        id = example.getId();
        updateDt = example.getUpdateDt();
        createDt = example.getCreateDt();
        Assertions.assertNotNull(id, "Пример с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление примера")
    protected void delete() {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + id + "/")
                .assertStatus(204);
    }

}
