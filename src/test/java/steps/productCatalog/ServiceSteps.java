package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.copyService.response.CopyServiceResponse;
import httpModels.productCatalog.createService.response.CreateServiceResponse;
import httpModels.productCatalog.getService.response.GetServiceResponse;
import io.qameta.allure.Step;
import org.json.JSONObject;

import static core.helper.JsonHelper.convertResponseOnClass;

public class ServiceSteps {

    public CreateServiceResponse createService(JSONObject body) {
        String serviceObject = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("services/", body)
                .toString();
        return convertResponseOnClass(serviceObject, CreateServiceResponse.class);
    }

    public void existService(String nameService) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("services/" + "exist/" + nameService)
                .assertStatus(200);
    }

    @Step("Получение сервиса по Id")
    public GetServiceResponse getServiceById(String id) {
        String serviceObject = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("services/" + id + "/")
                .assertStatus(200)
                .toString();
        return convertResponseOnClass(serviceObject, GetServiceResponse.class);
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
        String serviceObject = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("services/" + id + "/copy/")
                .assertStatus(200)
                .toString();
        return convertResponseOnClass(serviceObject, CopyServiceResponse.class);
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
}
