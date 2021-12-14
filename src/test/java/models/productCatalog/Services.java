package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.Service.createService.response.DataSource;
import httpModels.productCatalog.Service.createService.response.ExtraData;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import steps.productCatalog.ServiceSteps;

import java.util.List;

import static core.helper.JsonHelper.convertResponseOnClass;

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

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/services/createServices.json";
        graphId = new GraphSteps().getGraphId("AtTestServiceGraph");
        return this;
    }

    @Override
    public JSONObject toJson() {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", serviceName)
                .set("$.graph_id", graphId)
                .build();
    }

    @Override
    @Step("Создание сервиса")
    protected void create() {
        String response = new Http(Configure.ProductCatalogURL)
                
                .body(toJson())
                .post("services/")
                .assertStatus(201)
                .toString();
        CreateServiceResponse createServiceResponse = convertResponseOnClass(response, CreateServiceResponse.class);
        serviceId = createServiceResponse.getId();
        Assertions.assertNotNull(serviceId, "Сервис с именем: " + serviceName + ", не создался");
    }

    @Override
    @Step("Удаление сервиса")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                
                .delete("services/" + serviceId + "/")
                .assertStatus(204);

        ServiceSteps serviceSteps = new ServiceSteps();
        serviceId = serviceSteps.getServiceIdByName(serviceName);
        Assertions.assertNull(serviceId, String.format("Сервис с именем: %s не удалился", serviceName));
    }
}
