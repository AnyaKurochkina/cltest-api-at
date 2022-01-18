package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ExistImpl;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ProductCatalogSteps {

    @Step("Получение списка объекта продуктового каталога")
    public List<ItemImpl> getProductObjectList(String productName, Class<?> clazz) {
        return ((GetListImpl) new Http(Configure.ProductCatalogURL)
                .get(productName)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    @Step("Создание объекта продуктового каталога")
    public Http.Response createProductObject(String productName, JSONObject body) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .post(productName);
    }

    @Step("Проверка существования объекта продуктового каталога по имени")
    public boolean isExists(String productName, String name, Class<?> clazz) {
        return ((ExistImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + "exists/?name=" + name)
                .assertStatus(200)
                .extractAs(clazz)).isExist();
    }

    @Step("Импорт объекта продуктового каталога")
    public void importObject(String productName, String pathName) {
        new Http(Configure.ProductCatalogURL)
                .multiPart(productName + "obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Получение объекта продуктового каталога по Id")
    public GetImpl getById(String productName, String objectId, Class<?> clazz) {
        return (GetImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + objectId + "/")
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id")
    public void getByIdWithOutToken(String productName, String objectId, Class<?> clazz) {
        new Http(Configure.ProductCatalogURL).setWithoutToken()
                .get(productName + objectId + "/").assertStatus(403);
    }

    @Step("Обновление объекта продуктового каталога")
    public GetImpl patchObject(String productName, Class<?> clazz, String name, String graphId, String objectId) {
        return (GetImpl) new Http(Configure.ProductCatalogURL)
                .body(toJson("productCatalog/actions/createAction.json", name, graphId))
                .patch(productName + objectId + "/")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Копирование объекта продуктового каталога по Id")
    public void copyById(String productName, String objectId) {
        new Http(Configure.ProductCatalogURL)
                .post(productName + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Копирование объекта продуктового каталога по Id без ключа")
    public void copyByIdWithOutToken(String productName, String objectId) {
        new Http(Configure.ProductCatalogURL)
                .setWithoutToken()
                .post(productName + objectId + "/copy/")
                .assertStatus(403);
    }

    @Step("Экспорт объекта продуктового каталога по Id")
    public void exportById(String productName, String objectId) {
        new Http(Configure.ProductCatalogURL)
                .get(productName + objectId + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Удаление объекта продуктового каталога по имени")
    public void deleteByName(String productName, String name, Class<?> clazz) {
        deleteById(productName, getProductObjectIdByNameWithMultiSearch(productName, name, clazz));
    }

    @Step("Удаление объекта продуктового каталога по Id")
    public void deleteById(String productName, String objectId) {
        getDeleteObjectResponse(productName, objectId).assertStatus(204);
    }

    @Step("Удаление объекта продуктового каталога по Id без токена")
    public void deleteObjectByIdWithOutToken(String productName, String id) {
        new Http(Configure.ProductCatalogURL)
                .setWithoutToken()
                .delete(productName + id + "/").assertStatus(403);
    }

    @Step("Поиск ID экшена по имени с использованием multiSearch")
    public String getProductObjectIdByNameWithMultiSearch(String productName, String name, Class<?> clazz) {
        String objectId = null;
        List<ItemImpl> list = ((GetListImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + "?include=total_count&page=1&per_page=10&multisearch=" + name)
                .assertStatus(200).extractAs(clazz)).getItemsList();

        for (ItemImpl item : list) {
            if (item.getName().equals(name)) {
                objectId = item.getId();
                break;
            }
        }
        Assertions.assertNotNull(objectId, String.format("Объект с именем: %s, с помощью multiSearch не найден", name));
        return objectId;
    }

    @Step("ООбновление объекта продуктового каталога")
    public Http.Response patchRow(String productName, JSONObject body, String actionId) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .patch(productName + actionId + "/");
    }

    @Step("Создание JSON объекта продуктового каталога")
    public JSONObject createJsonObject(String name, String templatePath) {
        return JsonHelper
                .getJsonTemplate(templatePath)
                .set("$.name", name)
                .build();
    }

    @Step("Частичное обновление продукта")
    public Http.Response partialUpdateObject(String productName, String id, JSONObject object) {
        return new Http(Configure.ProductCatalogURL)
                .body(object)
                .patch(productName + id + "/");
    }

    @Step("Частичное обновление продукта без токена")
    public void partialUpdateObjectWithOutToken(String productName, String id, JSONObject object) {
        new Http(Configure.ProductCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(productName + id + "/")
                .assertStatus(403);
    }

    @Step("Получение времени отклика на запрос")
    public long getTime(String url) {
        Response response = given()
                .get(url);
        return response.getTime();
    }

    @Step("Получение массива объектов используещих граф")
    public JsonPath getObjectArrayUsedGraph(String id) {
        return new Http(Configure.ProductCatalogURL)
                .get("graphs/" + id + "/used/")
                .assertStatus(200).jsonPath();
    }

    public Http.Response getDeleteObjectResponse(String productName, String id) {
        return new Http(Configure.ProductCatalogURL)
                .delete(productName + id + "/");
    }

    public List<ItemImpl> getProductObjectList(String productName, Class<?> clazz, String filter) {
        return ((GetListImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + filter)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    public JsonPath getJsonPath(String productName, String id) {
        return new Http(Configure.ProductCatalogURL)
                .get(productName + id + "/")
                .assertStatus(200).jsonPath();
    }

    private JSONObject toJson(String pathToJsonBody, String actionName, String graphId) {
        return JsonHelper.getJsonTemplate(pathToJsonBody)
                .set("$.name", actionName)
                .set("$.title", actionName)
                .set("$.description", actionName)
                .set("$.graph_id", graphId)
                .set("$.graph_version_pattern", "1.")
                .build();
    }
}
