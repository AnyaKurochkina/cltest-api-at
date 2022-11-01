package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import io.qameta.allure.Step;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ServiceSteps extends Steps {

    private static String serviceUrl = "/api/v1/services/";

    @Step("Получение списка Сервисов продуктового каталога")
    public static List<ItemImpl> getServiceList() {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl)
                .compareWithJsonSchema("jsonSchema/getServiceListSchema.json")
                .assertStatus(200)
                .extractAs(GetServiceListResponse.class)).getItemsList();
    }

    @Step("Создание сервиса")
    public static Response createService(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(serviceUrl);
    }
}
