package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.createService.response.CreateServiceResponse;
import httpModels.productCatalog.createService.response.DataSource;
import httpModels.productCatalog.createService.response.ExtraData;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import steps.productCatalog.ProductsSteps;

import static core.helper.JsonHelper.convertResponseOnClass;

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
        String response = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("services/", toJson())
                .assertStatus(201)
                .toString();
        CreateServiceResponse createServiceResponse = convertResponseOnClass(response, CreateServiceResponse.class);
        serviceId = createServiceResponse.getId();
        Assertions.assertNotNull(serviceId, "Сервис с именем: " + serviceName + ", не создался");
    }

    @Override
    @Step("Удаление сервиса")
    protected void delete() {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .delete("services/" + serviceId + "/")
                .assertStatus(204);

        ProductsSteps productsSteps = new ProductsSteps();
        serviceId = productsSteps.getProductId(serviceName);
        Assertions.assertNull(serviceId, String.format("Сервис с именем: %s не удалился", serviceName));
    }

    @Step("Обновление сервиса")
    public void updateProduct() {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .patch("services/" + serviceId + "/",
                        this.getTemplate()
                                .set("$.description", "Update desc").build())
                .assertStatus(200);
    }
}
