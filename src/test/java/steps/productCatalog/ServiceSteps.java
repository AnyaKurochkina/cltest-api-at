package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import io.qameta.allure.Step;
import models.cloud.productCatalog.service.GetServiceList;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ServiceSteps extends Steps {

    private static String serviceUrl = "/api/v1/services/";

    @Step("Получение списка Сервисов продуктового каталога")
    public static List<Service> getServiceList() {
        return  new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl)
                .compareWithJsonSchema("jsonSchema/getServiceListSchema.json")
                .assertStatus(200)
                .extractAs(GetServiceList.class).getList();
    }

    @Step("Создание сервиса")
    public static Response createService(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(serviceUrl);
    }

    @Step("Получение сервиса по Id")
    public static GetServiceResponse getServiceById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl + objectId + "/")
                .extractAs(GetServiceResponse.class);
    }

    @Step("Получение сервиса по Id и фильтру {filter}")
    public static Service getServiceByIdAndFilter(String objectId, String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl + objectId + "/?{}", filter)
                .extractAs(Service.class);
    }

    @Step("Получение сервиса по Id под ролью Viewer")
    public static Response getServiceViewerById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(serviceUrl + objectId + "/");
    }
}
