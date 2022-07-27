package steps.productCatalog;

import core.helper.http.Http;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import io.qameta.allure.Step;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ServiceSteps extends Steps {

    @Step("Получение списка Сервисов продуктового каталога")
    public static List<ItemImpl> getServiceList() {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get("/api/v1/services/")
                .compareWithJsonSchema("jsonSchema/getServiceListSchema.json")
                .assertStatus(200)
                .extractAs(GetServiceListResponse.class)).getItemsList();
    }
}
