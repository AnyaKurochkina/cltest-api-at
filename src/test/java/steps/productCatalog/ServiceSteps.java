package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Service.copyService.response.CopyServiceResponse;
import httpModels.productCatalog.Service.existsService.response.ExistsServiceResponse;
import httpModels.productCatalog.Service.getService.response.GetServiceResponse;
import httpModels.productCatalog.Service.getServiceList.response.GetServiceListResponse;
import httpModels.productCatalog.Service.getServiceList.response.ListItem;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ServiceSteps {

    @SneakyThrows
    @Step("Создание сервиса")
    public Http.Response createService(JSONObject body) {
        return new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("services/", body);
    }

    @SneakyThrows
    @Step("Получение списка сервисов")
    public List<ListItem> getServicesList() {
        return new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .setWithoutToken()
                .get("services/")
                .assertStatus(200)
                .extractAs(GetServiceListResponse.class)
                .getList();
    }

    public boolean isServiceExist(String name) {
        return new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("services/exists/?name=" + name)
                .assertStatus(200)
                .extractAs(ExistsServiceResponse.class)
                .getExists();
    }

    @Step("Получение сервиса по Id")
    public GetServiceResponse getServiceById(String id) {
        return new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("services/" + id + "/")
                .assertStatus(200)
                .extractAs(GetServiceResponse.class);
    }

    @Step("Удаление сервиса по Id")
    public void deleteServiceById(String id) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .delete("services/" + id + "/")
                .assertStatus(204);
    }

    @Step("Копирование сервиса по Id")
    public CopyServiceResponse copyServiceById(String id) {
        return new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("services/" + id + "/copy/")
                .assertStatus(200)
                .extractAs(CopyServiceResponse.class);
    }

    @Step("Обновление сервиса")
    public void updateServiceById(String id) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .patch("services/" + id + "/",
                        new JsonHelper().getJsonTemplate("productCatalog/services/createServices.json")
                                .set("$.description", "Update desc")
                                .build())
                .assertStatus(200);
    }

    @Step("Получение ID сервиса по его имени: {serviceName}")
    public String getServiceIdByName(String serviceName) {
        String serviceId = null;
        List<ListItem> servicesList = getServicesList();
        for (ListItem listItem : servicesList) {
            if (listItem.getName().equals(serviceName)) {
                serviceId = listItem.getId();
                break;
            }
        }
        return serviceId;
    }

    @SneakyThrows
    @Step("Импорт сервиса")
    public void importService(String pathName) {
        ValidatableResponse response = given()
                .contentType("multipart/form-data")
                .multiPart("file", new File(pathName))
                .when()
                .post("http://dev-kong-service.apps.d0-oscp.corp.dev.vtb/product-catalog/services/obj_import/")
                .then()
                .statusCode(200);
    }

    @Step("Создание JSON объекта по сервисам")
    public JSONObject createJsonObject(String name) {
        return new JsonHelper()
                .getJsonTemplate("productCatalog/services/createServices.json")
                .set("$.name", name)
                .build();
    }
}
