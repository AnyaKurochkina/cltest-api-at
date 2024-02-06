package steps.productCatalog;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.pythonTemplate.GetPythonTemplateList;
import models.cloud.productCatalog.pythonTemplate.PythonTemplate;
import org.json.JSONObject;

import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static core.helper.JsonHelper.getStringFromFile;

public class PythonTemplateSteps {
    private static final String pythonTemplateV1 = "/api/v1/python_templates/";
    private static final String pythonTemplateV2 = "/api/v2/python_templates/";

    @Step("Создание python_template")
    public static PythonTemplate createPythonTemplate(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(pythonTemplateV1)
                .assertStatus(201)
                .extractAs(PythonTemplate.class);
    }

    @Step("Создание python_template по имени {name}")
    public static PythonTemplate createPythonTemplateByName(String name) {
        PythonTemplate pythonTemplate = JsonHelper.deserialize(getStringFromFile("productCatalog/pythonTemplate/createPythonTemplate.json"), PythonTemplate.class);
        pythonTemplate.setName(name);
        pythonTemplate.init();
        return  pythonTemplate.createObject();
    }

    @Step("Получение python_template по Id")
    public static PythonTemplate getPythonTemplateById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(pythonTemplateV1 + objectId + "/")
                .extractAs(PythonTemplate.class);
    }

    @Step("Проверка существования python_template по имени '{name}'")
    public static boolean isPythonTemplateExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(pythonTemplateV1 + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Удаление python_template")
    public static void deletePythonTemplate(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(pythonTemplateV1 + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление python_template")
    public static Response deletePythonTemplateResponse(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(pythonTemplateV1 + id + "/");
    }

    @Step("Удаление python_template по имени {name}")
    public static void deletePythonTemplateByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(pythonTemplateV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Частичное обновление python_template")
    public static Response partialUpdatePythonTemplate(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(pythonTemplateV1 + id + "/");
    }

    @Step("Обновление python_template")
    public static PythonTemplate updatePythonTemplate(String id, JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(pythonTemplateV1 + id + "/")
                .assertStatus(200).extractAs(PythonTemplate.class);
    }

    @Step("Получение списка python_template продуктового каталога")
    public static List<PythonTemplate> getPythonTemplateList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(pythonTemplateV1)
                .assertStatus(200)
                .extractAs(GetPythonTemplateList.class).getList();
    }

    @Step("Копирование python_template по {objectId}")
    public static PythonTemplate copyPythonTemplateById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(pythonTemplateV1 + objectId + "/copy/")
                .assertStatus(201)
                .extractAs(PythonTemplate.class);
    }

    @Step("Получение списка объектов использующих python_template")
    public static Response getObjectListUsedPythonTemplate(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(pythonTemplateV1 + objectId + "/used/")
                .assertStatus(200);
    }
}
