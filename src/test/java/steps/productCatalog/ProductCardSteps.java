package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.productCard.ProductCard;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ProductCardSteps extends Steps {

    private static final String cardUrl = "/api/v1/product_cards/";
    private static final String cardhUrl2 = "/api/v2/product_cards/";

    public static ProductCard createProductCard() {
        return ProductCard.builder()
                .title(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_api_test")
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_api_test")
                .build()
                .createObject();
    }

    @Step("Создание productCard")
    public static ProductCard createProductCard(JSONObject jsonObject) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .post(cardUrl)
                .assertStatus(201)
                .extractAs(ProductCard.class);
    }

    @Step("Удаление productCard по id {id}")
    public static void deleteProductCard(String id) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(cardUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Получение productCard по id {id}")
    public static ProductCard getProductCard(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(cardUrl + id + "/")
                .assertStatus(200)
                .extractAs(ProductCard.class);
    }

    @Step("Добавление списка Тегов product cards")
    public static void addTagListToProductCard(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("add_tags", tagsList))
                .post(cardUrl + "add_tag_list/?name__in=" + names)
                .assertStatus(200);
    }

    @Step("Удаление списка Тегов product cards")
    public static void removeTagListToProductCard(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("remove_tags", tagsList))
                .post(cardUrl + "remove_tag_list/?name__in=" + names)
                .assertStatus(200);
    }

    @Step("Частичное обновление product cards")
    public static Response partialUpdateProductCard(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(cardUrl + id + "/");
    }

}
