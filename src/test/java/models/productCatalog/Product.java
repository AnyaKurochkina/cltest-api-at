package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Product.createProduct.response.CreateProductResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import steps.productCatalog.ProductsSteps;

import java.util.List;

import static core.helper.JsonHelper.convertResponseOnClass;

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
    private String productName;
    private List<Object> restrictedPaths;
    private String graphVersionPattern;
    private List<Object> allowedGroups;
    private String productId;
    private String category;
    private String jsonTemplate;

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/products/createProduct.json";
        GraphSteps graphSteps = new GraphSteps();
        graphId = graphSteps.getGraphId("GraphProduct");
        return this;
    }

    @Override
    public JSONObject toJson() {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", productName)
                .set("$.title", title)
                .set("$.graph_id", graphId)
                .set("$.envs", new JSONArray(envs))
                .build();
    }

    @Override
    protected void create() {
        String response = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .post("products/", toJson())
                .assertStatus(201)
                .toString();

        CreateProductResponse createProductResponse = convertResponseOnClass(response, CreateProductResponse.class);
        productId = createProductResponse.getId();
        Assertions.assertNotNull(productId, "Продукт с именем: " + productName + ", не создался");
    }

    @Step("Обновление продукта")
    public void updateProduct() {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .patch("products/" + productId + "/",
                        this.getTemplate()
                                .set("$.version", "1.1.1").build())
                .assertStatus(200);
    }

    @Override
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .delete("products/" + productId + "/")
                .assertStatus(204);

        ProductsSteps productsSteps = new ProductsSteps();
        productId = productsSteps.getProductId(productName);
        Assertions.assertNull(productId, String.format("Продукт с именем: %s не удалился", productName));
    }
}