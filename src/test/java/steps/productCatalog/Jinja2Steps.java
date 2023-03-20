package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.jinja2.GetJinja2List;
import models.cloud.productCatalog.jinja2.Jinja2;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class Jinja2Steps extends Steps {
    private static final String jinjaUrl = "/api/v1/jinja2_templates/";
    private static final String jinjaUrl2 = "/api/v2/jinja2_templates/";

    @Step("Получение списка jinja2")
    public static List<Jinja2> getJinja2List() {
        //Todo сравнение с jsonshema
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl)
               // .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetJinja2List.class).getList();
    }

    @Step("Получение jinja2 по Id")
    public static Jinja2 getJinja2ById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + objectId + "/")
                .extractAs(Jinja2.class);
    }

    @Step("Проверка существования jinja2 по имени")
    public static boolean isJinja2Exists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Удаление jinja2 по имени {name}")
    public static void deleteJinjaByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(jinjaUrl2 + name + "/")
                .assertStatus(204);
    }

    @Step("Создание jinja2")
    public static Jinja2 createJinja(String name) {
        return Jinja2.builder()
                .name(name)
                .build()
                .createObject();
    }

    @Step("Экспорт jinja2 по Id")
    public static Response exportJinjaById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + objectId + "/obj_export/?as_file=true")
                .assertStatus(200);
    }
}
