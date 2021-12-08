package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.OrgDirection.existsOrgDirection.response.ExistsOrgDirectionResponse;
import httpModels.productCatalog.Product.getProduct.response.GetProductResponse;
import httpModels.productCatalog.Product.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.Product.getProducts.response.ListItem;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.util.List;

import static core.helper.JsonHelper.convertResponseOnClass;

public class ProductsSteps {

    @SneakyThrows
    @Step("Получение ID продукта по его имени: {actionName}")
    public String getProductId(String productName) {
        String productsId = null;
        for (ListItem listItem : getProductList()) {
            if (listItem.getName().equals(productName)) {
                productsId = listItem.getId();
                break;
            }
        }
        return productsId;
    }

    @Step("Получение списка продуктов")
    public List<ListItem> getProductList() {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("products/")
                .assertStatus(200)
                .toString();

        GetProductsResponse response = convertResponseOnClass(object, GetProductsResponse.class);
        return response.getList();
    }

    @Step("Проверка существования продукта с таким именем")
    public boolean isProductExist(String name) {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("/products/exists/?name=" + name)
                .assertStatus(200)
                .toString();
        ExistsOrgDirectionResponse response = convertResponseOnClass(object, ExistsOrgDirectionResponse.class);
        return response.getExists();
    }

    @Step("Ипорт продукта")
    public void importProduct(JSONObject jsonObject) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("/products/obj_import/", jsonObject)
                .assertStatus(201);
    }

    @Step("Создание JSON объекта по продуктам")
    public JSONObject createJsonObject(String name) {
        return new JsonHelper()
                .getJsonTemplate("productCatalog/products/createProduct.json")
                .set("$.name", name)
                .build();
    }

    @Step("Получение продукта по Id")
    public GetProductResponse getProductById(String id) {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("products/" + id + "/")
                .assertStatus(200)
                .toString();
        return convertResponseOnClass(object, GetProductResponse.class);
    }

    @Step("Частичное обновление продукта")
    public void partialUpdateProduct(String id, String key, String value) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .patch("products/" + id + "/", new JSONObject().put(key, value))
                .assertStatus(200);
    }
}
