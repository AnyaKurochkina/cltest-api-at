package models.productCatalog;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.service.createService.response.DataSource;
import httpModels.productCatalog.service.createService.response.ExtraData;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.productCatalog.graph.Graph;
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
    private String iconUrl;
    private String iconStoreId;
    private String description;
    private String serviceInfo;
    private String graphVersion;
    private String title;
    private String directionId;
    private String directionTitle;
    private String directionName;
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
    private String currentVersion;
    private Boolean autoOpenResults;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("/services/",
            "productCatalog/services/createServices.json");

    private final String productName = "/api/v1/services/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/services/createServices.json";
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_services_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        if (directionId == null) {
            OrgDirection orgDirection = OrgDirection.builder()
                    .orgDirectionName("direction_for_services_api_test")
                    .title("test_api")
                    .build()
                    .createObject();
            directionId = orgDirection.getOrgDirectionId();
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
                .set("$.current_version", currentVersion)
                .set("$.auto_open_results", autoOpenResults)
                .set("$.direction_id", directionId)
                .set("$.direction_name", directionName)
                .set("$.service_info", serviceInfo)
                .set("$.icon", icon)
                .set("$.icon_url", iconUrl)
                .set("$.icon_store_id", iconStoreId)
                .setIfNullRemove("$.start_btn_label", startBtnLabel)
                .build();
    }

    @Override
    @Step("Создание сервиса")
    protected void create() {
        if (productCatalogSteps.isExists(serviceName)) {
            productCatalogSteps.deleteByName(serviceName, GetServiceListResponse.class);
        }
        CreateServiceResponse createServiceResponse = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateServiceResponse.class);
        serviceId = createServiceResponse.getId();
        Assertions.assertNotNull(serviceId, "Сервис с именем: " + serviceName + ", не создался");
    }

    @Override
    @Step("Удаление сервиса")
    protected void delete() {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + serviceId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(serviceName));
    }
}
