package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.Service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.Service.createService.response.DataSource;
import httpModels.productCatalog.Service.createService.response.ExtraData;
import httpModels.productCatalog.Service.existsService.response.ExistsServiceResponse;
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
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();

    private final String productName = "services/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/services/createServices.json";
        graphId = productCatalogSteps
                .getProductObjectIdByNameWithMultiSearch("graphs/", "AtTestServiceGraph", GetGraphsListResponse.class);
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", serviceName)
                .set("$.graph_id", graphId)
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
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();
        Assertions.assertFalse(productCatalogSteps.isExists(productName, serviceName, ExistsServiceResponse.class));
    }
}
