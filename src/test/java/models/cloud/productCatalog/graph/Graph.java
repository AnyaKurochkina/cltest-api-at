package models.cloud.productCatalog.graph;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import httpModels.productCatalog.graphs.getUsedList.GetUsedListResponse;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.productCatalog.product.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;
import java.util.Map;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ActionSteps.deleteActionById;
import static steps.productCatalog.ActionSteps.isActionExists;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Graph extends Entity implements IProductCatalog {
    @JsonProperty("version_list")
    private List<String> versionList;
    @JsonProperty("author")
    private String author;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    @JsonProperty("description")
    private String description;
    @JsonProperty("damage_order_on_error")
    private Boolean damageOrderOnError;
    @JsonProperty("type")
    private String type;
    @JsonProperty("title")
    private String title;
    @JsonProperty("version")
    private String version;
    @JsonProperty("last_version")
    private String lastVersion;
    @JsonProperty("graph")
    private List<GraphItem> graph;
    @JsonProperty("output")
    private Map<String, String> output;
    @JsonProperty("printed_output")
    private Object printedOutput;
    @JsonProperty("json_schema")
    private Map<String, Object> jsonSchema;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    @JsonProperty("name")
    private String name;
    @JsonProperty("static_data")
    private Map<String, Object> staticData;
    @JsonProperty("id")
    private String graphId;
    @JsonProperty("ui_schema")
    private Map<String, Object> uiSchema;
    @JsonProperty("modifications")
    private List<Modification> modifications;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("is_sequential")
    private Boolean isSequential;
    @JsonProperty("allowed_developers")
    private List<String> allowedDevelopers;
    @JsonProperty("version_fields")
    private List<String> versionFields;
    @JsonProperty("restricted_developers")
    private List<String> restrictedDevelopers;
    @JsonProperty("current_version")
    private String currentVersion;
    @JsonProperty("lock_order_on_error")
    private Boolean lockOrderOnError;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("/api/v1/graphs/",
            "productCatalog/graphs/createGraph.json");

    @Override
    public Entity init() {
        return this;
    }

    @Override
    @SneakyThrows
    public JSONObject toJson() {
        JSONArray mod = null;
        if (modifications != null) {
            mod = new JSONArray(JsonHelper.getCustomObjectMapper().writeValueAsString(modifications));
        }
        return JsonHelper.getJsonTemplate("productCatalog/graphs/createGraph.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.type", type)
                .set("$.author", author)
                .set("$.graph", graph)
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
        Graph graph = createGraph(toJson());
        StringUtils.copyAvailableFields(graph, this);
        Assertions.assertNotNull(graphId, "Граф с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление графа")
    protected void delete() {
        deleteGraphById(graphId);
        Assertions.assertFalse(isGraphExists(name));
    }

    private void deleteIfExist(String name) {
        if (productCatalogSteps.isExists(name)) {
            String id = getGraphByName(name).getGraphId();
            List<GetUsedListResponse> list = getObjectArrayUsedGraph(id).getList("", GetUsedListResponse.class);
            for (GetUsedListResponse resp : list) {
                String type = resp.getType();
                switch (type) {
                    case ("Action"):
                        if (isActionExists(resp.getName())) {
                            deleteActionById(resp.getId());
                        }
                        break;
                    case ("Product"):
                        if (isProductExists(resp.getName())) {
                            Product product = getProductById(resp.getId());
                            if (product.getIsOpen()) {
                                partialUpdateProduct(product.getProductId(), new JSONObject().put("is_open", false));
                            }
                            deleteProductById(resp.getId());
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
                        if (isGraphExists(resp.getName())) {
                            deleteIfExist(resp.getName());
                        }
                        break;
                }
            }
            productCatalogSteps.getDeleteObjectResponse(id).assertStatus(204);
        }
    }
}
