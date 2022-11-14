package models.cloud.productCatalog.service;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.service.createService.response.DataSource;
import httpModels.productCatalog.service.createService.response.ExtraData;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Service extends Entity implements IProductCatalog {

    private final String productName = "/api/v1/services/";
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("/services/",
            "productCatalog/services/createServices.json");
    private String jsonTemplate;
    @JsonProperty("turn_off_inventory")
    private Boolean turnOffInventory;
    @JsonProperty("current_version")
    private String currentVersion;
    @JsonProperty("version_list")
    private List<String> versionList;
    @JsonProperty("is_published")
    private Boolean isPublished;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("icon_store_id")
    private String iconStoreId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("service_info")
    private String serviceInfo;
    @JsonProperty("graph_version")
    private String graphVersion;
    @JsonProperty("title")
    private String title;
    @JsonProperty("direction_id")
    private String directionId;
    @JsonProperty("inventory_actions")
    private List<Object> inventoryActions;
    @JsonProperty("graph_version_pattern")
    private String graphVersionPattern;
    @JsonProperty("hide_node_name_output")
    private Boolean hideNodeNameOutput;
    @JsonProperty("id")
    private String id;
    @JsonProperty("start_btn_label")
    private String startBtnLabel;
    @JsonProperty("version_create_dt")
    private String versionCreateDt;
    @JsonProperty("restricted_groups")
    private List<String> restrictedGroups;
    @JsonProperty("graph_id")
    private String graphId;
    @JsonProperty("version")
    private String version;
    @JsonProperty("data_source")
    private DataSource dataSource;
    @JsonProperty("check_rules")
    private List<Object> checkRules;
    @JsonProperty("auto_open_form")
    private Boolean autoOpenForm;
    @JsonProperty("last_version")
    private String lastVersion;
    @JsonProperty("extra_data")
    private ExtraData extraData;
    @JsonProperty("version_changed_by_user")
    private String versionChangedByUser;
    @JsonProperty("name")
    private String name;
    @JsonProperty("allowed_groups")
    private List<String> allowedGroups;
    @JsonProperty("version_fields")
    private List<String> versionFields;
    @JsonProperty("graph_version_calculated")
    private String graphVersionCalculated;
    @JsonProperty("create_dt")
    private String createDt;
    @JsonProperty("update_dt")
    private String updateDt;
    @JsonProperty("auto_open_results")
    private Boolean autoOpenResults;
    @JsonProperty("direction_title")
    private String directionTitle;
    @JsonProperty("allowed_developers")
    private List<String> allowedDevelopers;
    @JsonProperty("restricted_developers")
    private List<String> restrictedDevelopers;
    @JsonProperty("direction_name")
    private String directionName;
    private Graph graph;

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/services/createServices.json";
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_services_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        if (directionId == null) {
            OrgDirection orgDirection = OrgDirection.builder()
                    .name("direction_for_services_api_test")
                    .title("test_api")
                    .build()
                    .createObject();
            directionId = orgDirection.getId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.graph_id", graphId)
                .set("$.graph_version", graphVersion)
                .set("$.version", version)
                .set("$.is_published", isPublished)
                .set("$.title", title)
                .set("$.current_version", currentVersion)
                .set("$.auto_open_results", autoOpenResults)
                .set("$.direction_id", directionId)
                .set("$.direction_name", directionName)
                .set("$.service_info", serviceInfo)
                .set("$.icon_url", iconUrl)
                .setIfNullRemove("$.icon_store_id", iconStoreId)
                .setIfNullRemove("$.start_btn_label", startBtnLabel)
                .set("$.description", description)
                .set("$.restricted_groups", restrictedGroups)
                .set("$.allowed_groups", allowedGroups)
                .build();
    }

    @Override
    @Step("Создание сервиса")
    protected void create() {
        if (productCatalogSteps.isExists(name)) {
            productCatalogSteps.deleteByName(name, GetServiceListResponse.class);
        }
        CreateServiceResponse createServiceResponse = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateServiceResponse.class);
        id = createServiceResponse.getId();
        directionName = createServiceResponse.getDirectionName();
        Assertions.assertNotNull(id, "Сервис с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление сервиса")
    protected void delete() {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + id + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(name));
    }
}
