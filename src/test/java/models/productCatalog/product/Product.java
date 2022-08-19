package models.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.product.createProduct.response.CreateProductResponse;
import httpModels.productCatalog.product.getProducts.response.GetProductsResponse;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.productCatalog.graph.Graph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;
import java.util.Map;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Entity {

    @JsonProperty("allowed_paths")
    private List<Object> allowedPaths;
    @JsonProperty("is_open")
    private boolean isOpen;
    @JsonProperty("version_list")
    private List<String> versionList;
    private String author;
    @JsonProperty("current_version")
    private String currentVersion;
    @JsonProperty("information_systems")
    private List<Object> informationSystems;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("icon_store_id")
    private String iconStoreId;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    @JsonProperty("envs")
    private List<String> envs;
    @JsonProperty("description")
    private String description;
    @JsonProperty("graph_version")
    private String graphVersion;
    @JsonProperty("restricted_groups")
    private List<Object> restrictedGroups;
    private String title;
    @JsonProperty("graph_id")
    private String graphId;
    private String version;
    @JsonProperty("max_count")
    private int maxCount;
    private Integer number;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    private String name;
    @JsonProperty("restricted_paths")
    private List<Object> restrictedPaths;
    @JsonProperty("allowed_groups")
    private List<Object> allowedGroups;
    @JsonProperty("graph_version_pattern")
    private String graphVersionPattern;
    @JsonProperty("id")
    private String productId;
    @JsonProperty("graph_version_calculated")
    private String graphVersionCalculated;
    private String category;
    @JsonProperty("category_v2")
    private Categories categoryV2;
    @JsonProperty("last_version")
    private String lastVersion;
    private Map<String, String> info;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("extra_data")
    private Map<String, String> extraData;
    @JsonProperty("in_general_list")
    private Boolean inGeneralList;
    @JsonProperty("allowed_developers")
    private List<String> allowedDevelopers;
    @JsonProperty("restricted_developers")
    private List<String> restrictedDevelopers;
    private String payment;
    private String jsonTemplate;

    public static final String productName = "/api/v1/products/";
    @Builder.Default
    protected transient ProductCatalogSteps steps = new ProductCatalogSteps(productName,
            "productCatalog/products/createProduct.json");

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/products/createProduct.json";
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_product_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        String categor = null;
        if (categoryV2 != null) {
            categor = categoryV2.getValue();
        }
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.graph_id", graphId)
                .set("$.envs", new JSONArray(envs))
                .set("$.version", version)
                .set("$.category", category)
                .set("$.info", info)
                .set("$.icon", icon)
                .set("$.icon_store_id", iconStoreId)
                .set("$.icon_url", iconUrl)
                .set("$.is_open", isOpen)
                .set("$.current_version", currentVersion)
                .set("$.extra_data", extraData)
                .set("$.information_systems", informationSystems)
                .set("$.in_general_list", inGeneralList)
                .set("$.payment", payment)
                .setIfNullRemove("$.category_v2", categor)
                .setIfNullRemove("$.number", number)
                .build();
    }

    @Override
    @Step("Создание продукта")
    protected void create() {
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetProductsResponse.class);
        }
        CreateProductResponse createProductResponse = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateProductResponse.class);
        productId = createProductResponse.getId();
        Assertions.assertNotNull(productId, "Продукт с именем: " + name + ", не создался");
    }

    @Step("Обновление продукта")
    public void updateProduct() {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(this.getTemplate().set("$.version", "1.1.1").build())
                .patch(productName + productId + "/")
                .assertStatus(200);
    }

    @Override
    protected void delete() {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + productId + "/")
                .assertStatus(204);
        ProductCatalogSteps steps = new ProductCatalogSteps(productName,"productCatalog/products/createProduct.json");
        Assertions.assertFalse(steps.isExists(name));
    }
}