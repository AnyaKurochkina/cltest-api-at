package models.productCatalog;

import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.service.createService.response.DataSource;
import httpModels.productCatalog.service.createService.response.ExtraData;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter

public class Services extends Entity {

    private Boolean turnOffInventory;
    private List<String> versionList;
    private Boolean isPublished;
    private String icon;
    private String description;
    private String serviceInfo;
    private String graphVersion;
    private String title;
    private String directionId;
    private List<Object> inventoryActions;
    private String graphVersionPattern;
    private Boolean hideNodeNameOutput;
    private String direction;
    private Object startBtnLabel;
    private String versionCreateDt;
    private List<String> restrictedGroups;
    private String graphId;
    private String version;
    private DataSource dataSource;
    private List<Object> checkRules;
    private Boolean autoOpenForm;
    private String lastVersion;
    private ExtraData extraData;
    private String versionChangedByUser;
    private String serviceName;
    private List<String> allowedGroups;
    private String graphVersionCalculated;
    private String serviceId;
    private String jsonTemplate;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("services/",
            "productCatalog/services/createServices.json", ProductCatalogURL + "/api/v1/");

    private final String productName = "services/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/services/createServices.json";
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_services_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", serviceName)
                .set("$.graph_id", graphId)
                .set("$.version", version)
                .set("$.is_published", isPublished)
                .set("$.title", title)
                .build();
    }

    @Override
    @Step("Создание сервиса")
    protected void create() {
        CreateServiceResponse createServiceResponse = new Http(ProductCatalogURL + "/api/v1/")
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
        new Http(ProductCatalogURL + "/api/v1/")
                .delete("services/" + serviceId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate, ProductCatalogURL + "/api/v1/");
        Assertions.assertFalse(productCatalogSteps.isExists(serviceName));
    }
}
