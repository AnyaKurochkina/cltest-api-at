package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.service.createService.response.DataSource;
import httpModels.productCatalog.service.createService.response.ExtraData;
import httpModels.productCatalog.service.existsService.response.ExistsServiceResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

@Log4j2
@Builder
@Getter

public class Services extends Entity {

    private List<Object> allowedPaths;
    private Boolean isPublished;
    private Object icon;
    private String graphVersion;
    private String description;
    private String version;
    private List<Object> restrictedGroups;
    private String graphId;
    private DataSource dataSource;
    private Integer number;
    private String directionId;
    private ExtraData extraData;
    private String serviceName;
    private List<Object> restrictedPaths;
    private String graphVersionPattern;
    private List<Object> allowedGroups;
    private String serviceId;
    private String jsonTemplate;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("services/", "productCatalog/services/createServices.json" );

    private final String productName = "services/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/services/createServices.json";
        ProductCatalogSteps graphSteps = new ProductCatalogSteps("graphs/", "productCatalog/graphs/createGraph.json");
        graphId = graphSteps
                .getProductObjectIdByNameWithMultiSearch("graph_for_api_test", GetGraphsListResponse.class);
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", serviceName)
                .set("$.graph_id", graphId)
                .set("$.version", version)
                .build();
    }

    @Override
    @Step("Создание сервиса")
    protected void create() {
        CreateServiceResponse createServiceResponse = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post("services/")
                .assertStatus(201)
                .extractAs(CreateServiceResponse.class);
        serviceId = createServiceResponse.getId();
        Assertions.assertNotNull(serviceId, "Сервис с именем: " + serviceName + ", не создался");
    }

    @Override
    @Step("Удаление сервиса")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete("services/" + serviceId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(serviceName, ExistsServiceResponse.class));
    }
}
