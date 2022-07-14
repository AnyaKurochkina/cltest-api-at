package models.productCatalog.graph;

import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.graphs.createGraph.response.CreateGraphResponse;
import httpModels.productCatalog.graphs.createGraph.response.JsonSchema;
import httpModels.productCatalog.graphs.createGraph.response.StaticData;
import httpModels.productCatalog.graphs.createGraph.response.UiSchema;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.graphs.getUsedList.GetUsedListResponse;
import httpModels.productCatalog.product.getProduct.response.GetProductResponse;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter
public class Graph extends Entity {

    private String author;
    private JsonSchema jsonSchema;
    private String name;
    private String description;
    private StaticData staticData;
    private String graphId;
    private String title;
    private String type;
    private UiSchema uiSchema;
    private String version;
    private String jsonTemplate;
    private String createDt;
    private String updateDt;
    private Boolean damageOrderOnError;
    private Boolean lockOrderOnError;
    private List<String> allowedDevelopers;
    private List<String> restrictedDevelopers;
    private List<Modification> modifications;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("/api/v1/graphs/",
            "productCatalog/graphs/createGraph.json");

    public static final String productName = "/api/v1/graphs/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/graphs/createGraph.json";
        return this;
    }

    @Override
    @SneakyThrows
    public JSONObject toJson() {
        JSONArray mod = null;
        if (modifications != null) {
            mod = new JSONArray(JsonHelper.getCustomObjectMapper().writeValueAsString(modifications));
        }

        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.type", type)
                .set("$.author", author)
                .set("$.version", version)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .set("$.damage_order_on_error", damageOrderOnError)
                .set("$.lock_order_on_error", lockOrderOnError)
                .set("$.allowed_developers", allowedDevelopers)
                .set("$.restricted_developers", restrictedDevelopers)
                .setIfNullRemove("$.modifications", mod)
                .build();
    }

    @Override
    @Step("Создание графа")
    protected void create() {
        deleteIfExist(name);
        graphId = new Http(ProductCatalogURL)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateGraphResponse.class)
                .getId();
        Assertions.assertNotNull(graphId, "Граф с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление графа")
    protected void delete() {
        new Http(ProductCatalogURL)
                .delete(productName + graphId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(name));
    }

    private void deleteIfExist(String name) {
        if (productCatalogSteps.isExists(name)) {
            String id = productCatalogSteps.getProductObjectIdByNameWithMultiSearch(name, GetGraphsListResponse.class);
            List<GetUsedListResponse> list = productCatalogSteps.getObjectArrayUsedGraph(id).getList("", GetUsedListResponse.class);
            for (GetUsedListResponse resp : list) {
                String type = resp.getType();
                switch (type) {
                    case ("Action"):
                        ProductCatalogSteps actionSteps = new ProductCatalogSteps("/actions/", ProductCatalogURL + "/api/v1/");
                        if (actionSteps.isExists(resp.getName())) {
                            actionSteps.deleteById(resp.getId());
                        }
                        break;
                    case ("Product"):
                        ProductCatalogSteps productSteps = new ProductCatalogSteps("/products/", ProductCatalogURL + "/api/v1/");
                        if (productSteps.isExists(resp.getName())) {
                            GetProductResponse getProduct = (GetProductResponse) productSteps.getById(resp.getId(), GetProductResponse.class);
                            if (getProduct.isOpen()) {
                                productSteps.partialUpdateObject(getProduct.getId(), new JSONObject().put("is_open", false));
                            }
                            productSteps.deleteById(resp.getId());
                        }
                        break;
                    case ("Service"):
                        ProductCatalogSteps serviceSteps = new ProductCatalogSteps("/services/", ProductCatalogURL + "/api/v1/");
                        if (serviceSteps.isExists(resp.getName())) {
                            GetServiceResponse getService = (GetServiceResponse) serviceSteps.getById(resp.getId(), GetServiceResponse.class);
                            if (getService.getIsPublished()) {
                                serviceSteps.partialUpdateObject(getService.getId(), new JSONObject().put("is_published", false));
                            }
                            serviceSteps.deleteById(resp.getId());
                        }
                        break;
                    case ("Graph"):
                        ProductCatalogSteps graphSteps = new ProductCatalogSteps("/graphs/", ProductCatalogURL + "/api/v1/");
                        if (graphSteps.isExists(resp.getName())) {
                            deleteIfExist(resp.getName());
                            //  graphSteps.deleteById(resp.getId());
                        }
                        break;
                }
            }
            productCatalogSteps.getDeleteObjectResponse(id).assertStatus(200);
        }
    }
}
