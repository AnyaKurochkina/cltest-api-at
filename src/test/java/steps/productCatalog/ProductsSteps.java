package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import httpModels.productCatalog.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.getProducts.response.ListItem;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import static core.helper.JsonHelper.convertResponseOnClass;

public class ProductsSteps {

    @SneakyThrows
    @Step("Получение ID продукта по его имени: {actionName}")
    public String getProductId(String productName) {
        String productsId = null;
        String object = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("products/")
                .assertStatus(200)
                .toString();

        GetProductsResponse response = convertResponseOnClass(object, GetProductsResponse.class);

        for (ListItem listItem : response.getList()) {
            if (listItem.getName().equals(productName)) {
                productsId = listItem.getId();
                break;
            }
        }
        return productsId;
    }
}
