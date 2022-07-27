package steps.productCatalog;

import core.helper.http.Http;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.product.getProducts.response.GetProductsResponse;
import io.qameta.allure.Step;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ProductSteps extends Steps {
    @Step("Получение списка Продуктов продуктового каталога")
    public static List<ItemImpl> getProductList() {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get("/api/v1/products/")
                .compareWithJsonSchema("jsonSchema/getProductListSchema.json")
                .assertStatus(200)
                .extractAs(GetProductsResponse.class)).getItemsList();
    }
}
