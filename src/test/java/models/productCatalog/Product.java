package models.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.product.createProduct.response.CreateProductResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;
import java.util.Map;

@Log4j2
@Builder
@Getter
public class Product extends Entity {

    private List<Object> allowedPaths;
    private Boolean isOpen;
    private String author;
    private List<String> informationSystems;
    private String icon;
    private String description;
    private String graphVersion;
    private List<String> envs;
    private List<Object> restrictedGroups;
    private String title;
    private String graphId;
    private String version;
    private Integer maxCount;
    private String name;
    private List<Object> restrictedPaths;
    private String graphVersionPattern;
    private List<Object> allowedGroups;
    private String productId;
    private String category;
    private String jsonTemplate;
    private Map<String, String> info;

    public static final String productName = "products/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/products/createProduct.json";
        Graph graph = Graph.builder().name("graph_for_product_api_test").build().createObject();
        graphId = graph.getGraphId();
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.graph_id", graphId)
                .set("$.envs", new JSONArray(envs))
                .set("$.version", version)
                .set("$.category", category)
                .set("$.info", info)
                .set("$.is_open", isOpen)
                .build();
    }

    @Override
    protected void create() {
        CreateProductResponse createProductResponse = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post("products/")
                .assertStatus(201)
                .extractAs(CreateProductResponse.class);
        productId = createProductResponse.getId();
        Assertions.assertNotNull(productId, "Продукт с именем: " + name + ", не создался");
    }

    @Step("Обновление продукта")
    public void updateProduct() {
        new Http(Configure.ProductCatalogURL)
                .body(this.getTemplate().set("$.version", "1.1.1").build())
                .patch("products/" + productId + "/")
                .assertStatus(200);
    }

    @Override
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + productId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(productName));
    }
}