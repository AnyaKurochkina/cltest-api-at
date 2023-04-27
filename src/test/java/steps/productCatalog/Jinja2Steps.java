package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.jinja2.GetJinja2List;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class Jinja2Steps extends Steps {
    private static final String jinjaUrl = "/api/v1/jinja2_templates/";
    private static final String jinjaUrl2 = "/api/v2/jinja2_templates/";

    @Step("Получение списка jinja2")
    public static List<Jinja2Template> getJinja2List() {
        //Todo сравнение с jsonshema
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl)
               // .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetJinja2List.class).getList();
    }

    @Step("Получение jinja2 по Id")
    public static Jinja2Template getJinja2ById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + objectId + "/")
                .extractAs(Jinja2Template.class);
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

    @Step("Создание шаблона Jinja2")
    public static Jinja2Template createJinja(String name) {
        return Jinja2Template.builder()
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

    @Step("Получение Meta данных списка jinja2 продуктового каталога")
    public static Meta getMetaJinja2List() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl)
                .assertStatus(200)
                .extractAs(GetJinja2List.class).getMeta();
    }
}
