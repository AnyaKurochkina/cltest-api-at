package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.ExistImpl;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.itemVisualItem.getVisualTemplate.GetVisualTemplateResponse;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

@AllArgsConstructor
public class ProductCatalogSteps {
    String productName;
    String templatePath;

    @Step("Получение списка объекта продуктового каталога")
    public List<ItemImpl> getProductObjectList(Class<?> clazz) {
        return ((GetListImpl) new Http(Configure.ProductCatalogURL)
                .get(productName)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    @Step("Создание объекта продуктового каталога")
    public Http.Response createProductObject(JSONObject body) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .post(productName);
    }

    @Step("Создание объекта продуктового каталога")
    public Http.Response createProductObject(String url, JSONObject body) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .post(url);
    }

    @Step("Проверка существования объекта продуктового каталога по имени")
    public boolean isExists(String name, Class<?> clazz) {
        return ((ExistImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + "exists/?name=" + name)
                .assertStatus(200)
                .extractAs(clazz)).isExist();
    }

    @Step("Импорт объекта продуктового каталога")
    public void importObject(String pathName) {
        new Http(Configure.ProductCatalogURL)
                .multiPart(productName + "obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Получение объекта продуктового каталога по Id")
    public GetImpl getById(String objectId, Class<?> clazz) {
        return (GetImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + objectId + "/")
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id и по версии объекта")
    public GetImpl getByIdAndVersion(String objectId, String version, Class<?> clazz) {
        return (GetImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + objectId + "/?version=" + version)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id")
    public void getByIdWithOutToken(String objectId) {
        new Http(Configure.ProductCatalogURL).setWithoutToken()
                .get(productName + objectId + "/").assertStatus(401);
    }

    @Step("Обновление объекта продуктового каталога")
    public GetImpl patchObject(Class<?> clazz, String name, String graphId, String objectId) {
        return (GetImpl) new Http(Configure.ProductCatalogURL)
                .body(toJson("productCatalog/actions/createAction.json", name, graphId))
                .patch(productName + objectId + "/")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Копирование объекта продуктового каталога по Id")
    public void copyById(String objectId) {
        new Http(Configure.ProductCatalogURL)
                .post(productName + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Копирование объекта продуктового каталога по Id без ключа")
    public void copyByIdWithOutToken(String objectId) {
        new Http(Configure.ProductCatalogURL)
                .setWithoutToken()
                .post(productName + objectId + "/copy/")
                .assertStatus(403);
    }

    @Step("Экспорт объекта продуктового каталога по Id")
    public void exportById(String objectId) {
        new Http(Configure.ProductCatalogURL)
                .get(productName + objectId + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Удаление объекта продуктового каталога по имени")
    public void deleteByName(String name, Class<?> clazz) {
        deleteById(getProductObjectIdByNameWithMultiSearch(name, clazz));
    }

    @Step("Удаление объекта продуктового каталога по Id")
    public void deleteById(String objectId) {
        getDeleteObjectResponse(objectId).assertStatus(204);
    }

    @Step("Удаление объекта продуктового каталога по Id")
    public void deleteById(String url, String objectId) {
        getDeleteObjectResponse(url, objectId).assertStatus(204);
    }

    @Step("Удаление объекта продуктового каталога по Id без токена")
    public void deleteObjectByIdWithOutToken(String id) {
        new Http(Configure.ProductCatalogURL)
                .setWithoutToken()
                .delete(productName + id + "/").assertStatus(403);
    }

    @Step("Поиск ID объекта продуктового каталога по имени с использованием multiSearch")
    public String getProductObjectIdByNameWithMultiSearch(String name, Class<?> clazz) {
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
    public Http.Response patchRow(JSONObject body, String actionId) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .patch(productName + actionId + "/");
    }

    @Step("Создание JSON объекта продуктового каталога")
    public JSONObject createJsonObject(String name) {
        return JsonHelper
                .getJsonTemplate(templatePath)
                .set("$.name", name)
                .build();
    }

    @Step("Частичное обновление продукта")
    public Http.Response partialUpdateObject(String id, JSONObject object) {
        return new Http(Configure.ProductCatalogURL)
                .body(object)
                .patch(productName + id + "/");
    }

    @Step("Частичное обновление продукта без токена")
    public void partialUpdateObjectWithOutToken(String id, JSONObject object) {
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

    public Http.Response getDeleteObjectResponse(String id) {
        return new Http(Configure.ProductCatalogURL)
                .delete(productName + id + "/");
    }

    public Http.Response getDeleteObjectResponse(String url, String id) {
        return new Http(Configure.ProductCatalogURL)
                .delete(url + id + "/");
    }

    public List<ItemImpl> getProductObjectList(Class<?> clazz, String filter) {
        return ((GetListImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + filter)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    public JsonPath getJsonPath(String id) {
        return new Http(Configure.ProductCatalogURL)
                .get(productName + id + "/")
                .assertStatus(200).jsonPath();
    }

    @Step("Получение объекта продуктового каталога по имени")
    public GetListImpl getObjectByName(String name, Class<?> clazz) {
        return (GetListImpl) new Http(Configure.ProductCatalogURL)
                .get(productName + "?name=" + name)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение шаблона визуализации по event_type и event_provider")
    public GetVisualTemplateResponse getItemVisualTemplate(String eventType, String eventProvider) {
        return new Http(Configure.ProductCatalogURL)
                .get("item_visual_templates/item_visual_template/" + eventType + "/" + eventProvider + "/")
                .assertStatus(200)
                .extractAs(GetVisualTemplateResponse.class);
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
