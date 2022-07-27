package steps.productCatalog;

import core.helper.http.Http;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.action.getActionList.response.GetActionsListResponse;
import io.qameta.allure.Step;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ActionSteps extends Steps {

    @Step("Получение списка Действий продуктового каталога")
    public static List<ItemImpl> getActionList() {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get("/api/v1/actions/")
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetActionsListResponse.class)).getItemsList();
    }
}
