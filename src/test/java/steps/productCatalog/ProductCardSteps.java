package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.productCard.CardItems;
import models.cloud.productCatalog.productCard.ProductCard;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static core.helper.StringUtils.convertStringVersionToIntArrayVersion;

public class ProductCardSteps extends Steps {

    private static final String CARD_URL = "/api/v1/product_cards/";
    private static final String CARD_URL_V2 = "/api/v2/product_cards/";

    public static ProductCard createProductCard() {
        return ProductCard.builder()
                .title(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_api_test")
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_api_test")
                .build()
                .createObject();
    }

    @Step("Создание productCard")
    public static ProductCard createProductCard(JSONObject jsonObject) {
        return uncheckedCreateProductCard(jsonObject)
                .assertStatus(201)
                .extractAs(ProductCard.class);
    }

    @Step("Создание productCard")
    public static Response uncheckedCreateProductCard(JSONObject jsonObject) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .post(CARD_URL);
    }

    @Step("Удаление productCard по id {id}")
    public static void deleteProductCard(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(CARD_URL + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление productCard по имени {name}")
    public static void deleteProductCardByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(CARD_URL_V2 + name + "/")
                .assertStatus(204);
    }

    @Step("Получение productCard по id {id}")
    public static ProductCard getProductCard(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(CARD_URL + id + "/")
                .assertStatus(200)
                .extractAs(ProductCard.class);
    }

    @Step("Добавление списка Тегов product cards")
    public static void addTagListToProductCard(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("add_tags", tagsList))
                .post(CARD_URL + "add_tag_list/?name__in=" + names)
                .assertStatus(200);
    }

    @Step("Удаление списка Тегов product cards")
    public static void removeTagListToProductCard(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("remove_tags", tagsList))
                .post(CARD_URL + "remove_tag_list/?name__in=" + names)
                .assertStatus(200);
    }

    @Step("Частичное обновление product cards")
    public static Response partialUpdateProductCard(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(CARD_URL + id + "/");
    }

    @Step("Обновление product cards")
    public static Response updateProductCard(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .put(CARD_URL + id + "/")
                .assertStatus(200);
    }

    @Step("Применение product cards")
    public static void applyProductCard(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(CARD_URL + id + "/apply/")
                .assertStatus(200);
    }

    @Step("Копирование product cards")
    public static ProductCard copyProductCard(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(CARD_URL + id + "/copy/")
                .assertStatus(200)
                .extractAs(ProductCard.class);
    }

    @Step("Проверка существования product cards по имени {name}")
    public static boolean isProductCardExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(CARD_URL + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Экспорт product cards")
    public static Response exportProductCard(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(CARD_URL + id + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Импорт product cards")
    public static ImportObject importProductCard(String pathName) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(CARD_URL + "obj_import/", "file", new File(pathName))
                .assertStatus(200)
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Создание product cards с CardItems")
    public static ProductCard createProductCard(String name, CardItems... items) {
        return ProductCard.builder()
                .name(name)
                .cardItems(Arrays.asList(items))
                .build()
                .createObject();
    }

    @Step("Создание CardItem c типом {objType}")
    public static CardItems createCardItem(String objType, String objId, String version) {
        return CardItems.builder()
                .objType(objType)
                .objId(objId)
                .versionArr(convertStringVersionToIntArrayVersion(version))
                .build();
    }
}
