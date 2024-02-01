package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.template.GetTemplateList;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateSteps extends Steps {

    private static final String templateUrl = "/api/v1/templates/";
    private static final String templateUrlV2 = "/api/v2/templates/";

    @Step("Создание шаблона")
    public static Response createTemplate(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(templateUrl);
    }

    @Step("Копирование направления по Id")
    public static void copyTemplateById(Integer objectId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(templateUrl + objectId + "/copy/")
                .assertStatus(201);
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
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "{}/used/", id)
                .compareWithJsonSchema("jsonSchema/template/getNodesUsedTemplateSchema.json")
                .assertStatus(200);
    }

    @Step("Получение списка шаблонов")
    //todo сравнение с jsonShema
    public static List<Template> getTemplateList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl)
                .assertStatus(200)
                .extractAs(GetTemplateList.class).getList();
    }

    public static GetTemplateList getTemplatesList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl)
                .assertStatus(200)
                .extractAs(GetTemplateList.class);
    }

    @Step("Проверка существования шаблона по имени")
    public static boolean isTemplateExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Удаление шаблона по Id")
    public static void deleteTemplateById(Integer id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(templateUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Получение шаблона по имени")
    public static Template getTemplateByName(String name) {
        List<Template> list = new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(templateUrl + "?{}", "name=" + name)
                .extractAs(GetTemplateList.class).getList();
        assertEquals(name, list.get(0).getName());
        return list.get(0);
    }

    @Step("Получение шаблона по Id")
    public static Template getTemplateById(Integer objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(templateUrl + objectId + "/")
                .extractAs(Template.class);
    }

    @Step("Удаление шаблона по имени {name}")
    public static void deleteTemplateByName(String name) {
        new Http(productCatalogURL)
                .withServiceToken()
                .delete(templateUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Импорт шаблона")
    public static ImportObject importTemplate(String pathName) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(templateUrl + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Экспорт шаблона по Id {id}")
    public static Response exportTemplateById(Integer id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + id + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Экспорт шаблона по имени {name}")
    public static void exportTemplateByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrlV2 + name + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Добавление списка Тегов шаблонам")
    public static void addTagListToTemplate(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("add_tags", tagsList))
                .post(templateUrl + "add_tag_list/?name__in=" + names)
                .assertStatus(201);
    }

    @Step("Удаление списка Тегов шаблонов")
    public static void removeTagListToTemplate(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("remove_tags", tagsList))
                .post(templateUrl + "remove_tag_list/?name__in=" + names)
                .assertStatus(204);
    }

    @Step("Частичное обновление шаблона")
    public static Response partialUpdateTemplate(Integer id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(templateUrl + id + "/");
    }

    @Step("Получение списка шаблонов по фильтру")
    public static List<Template> getTemplateListByFilter(String filter, Object value) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "?{}={}", filter, value)
                .assertStatus(200)
                .extractAs(GetTemplateList.class)
                .getList();
    }

    @Step("Получение списка шаблонов по фильтрам")
    public static List<Template> getTemplateListByFilters(String...filter) {
        String filters = String.join("&", filter);
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "?" + filters)
                .assertStatus(200)
                .extractAs(GetTemplateList.class)
                .getList();
    }

    @Step("Сортировка шаблонов по дате создания")
    public static boolean orderingTemplateByCreateData() {
        List<Template> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(GetTemplateList.class).getList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
            if (!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
                return false;
            }
        }
        return true;
    }

    @Step("Сортировка шаблонов по дате создания")
    public static boolean orderingTemplateByUpdateData() {
        List<Template> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(GetTemplateList.class).getList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpdateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpdateDt());
            if (!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
                return false;
            }
        }
        return true;
    }

    @Step("Загрузка шаблона в Gitlab")
    public static Response dumpTemplateToBitbucket(Integer id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(templateUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка шаблона из Gitlab")
    public static Response loadTemplateFromBitbucket(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(templateUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }
}
