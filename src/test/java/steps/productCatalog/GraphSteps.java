package steps.productCatalog;

import core.helper.http.Http;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import io.qameta.allure.Step;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class GraphSteps extends Steps {

    @Step("Получение списка Графов продуктового каталога")
    public static List<ItemImpl> getGraphList() {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get("/api/v1/graphs/")
                .compareWithJsonSchema("jsonSchema/getGraphListSchema.json")
                .assertStatus(200)
                .extractAs(GetGraphsListResponse.class)).getItemsList();
    }
}
