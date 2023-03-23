package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.template.GetTemplateList;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateSteps extends Steps {

    private static final String templateUrl = "/api/v1/templates/";
    private static final String templateUrlV2 = "/api/v2/templates/";

    @Step("Создание шаблона")
    public static Response createTemplate(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(templateUrl);
    }

    @Step("Создание шаблона по имени {name}")
    public static Template createTemplateByName(String name) {
        return Template.builder()
                .name(name)
                .build()
                .createObject();
    }

    @Step("Полуение списка узлов использующих шаблон")
    public static Response getNodeListUsedTemplate(Integer id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "{}/used/", id)
                .compareWithJsonSchema("jsonSchema/template/getNodesUsedTemplateSchema.json")
                .assertStatus(200);
    }

    @Step("Получение списка шаблонов")
    //todo сравнение с jsonShema
    public static List<Template> getTemplateList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl)
                .assertStatus(200)
                .extractAs(GetTemplateList.class).getList();
    }

    @Step("Проверка существования шаблона по имени")
    public static boolean isTemplateExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Удаление шаблона по Id")
    public static void deleteTemplateById(Integer id) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(templateUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Получение шаблона по имени")
    public static Template getTemplateByName(String name) {
        List<Template> list = new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(templateUrl + "?{}", "name=" + name)
                .extractAs(GetTemplateList.class).getList();
        assertEquals(name, list.get(0).getName());
        return list.get(0);
    }

    @Step("Получение шаблона по Id")
    public static Template getTemplateById(Integer objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(templateUrl + objectId + "/")
                .extractAs(Template.class);
    }

    @Step("Удаление шаблона по имени {name}")
    public static void deleteTemplateByName(String name) {
        new Http(ProductCatalogURL)
                .withServiceToken()
                .delete(templateUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Импорт шаблона")
    public static Response importTemplate(String pathName) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(templateUrl + "obj_import/", "file", new File(pathName));
    }

    @Step("Экспорт шаблона по Id {id}")
    public static Response exportTemplateById(Integer id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + id + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Экспорт шаблона по имени {name}")
    public static void exportTemplateByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrlV2 + name + "/obj_export/")
                .assertStatus(200);
    }
}
