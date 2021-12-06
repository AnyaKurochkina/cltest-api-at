package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import httpModels.productCatalog.Product.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.Product.getProducts.response.ListItem;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import static core.helper.JsonHelper.convertResponseOnClass;

public class ProductsSteps {

    @SneakyThrows
    @Step("Получение ID продукта по его имени: {actionName}")
    public String getProductId(String productName) {
        String productsId = null;
        GetProductsResponse response = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("products/")
                .assertStatus(200)
                .extractAs(GetProductsResponse.class);

        for (ListItem listItem : response.getList()) {
            if (listItem.getName().equals(productName)) {
                productsId = listItem.getId();
                break;
            }
        }
        return productsId;
    }
}
