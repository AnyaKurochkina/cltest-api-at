package steps.productCatalog;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.MetaImpl;
import httpModels.productCatalog.itemVisualItem.getVisualTemplate.GetVisualTemplateResponse;
import httpModels.productCatalog.productOrgInfoSystem.createInfoSystem.CreateInfoSystemResponse;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static core.helper.Configure.ProductCatalogURL;
import static io.restassured.RestAssured.given;

@Data
@AllArgsConstructor
public class ProductCatalogSteps {
    String productName;
    String templatePath;

    public ProductCatalogSteps(String productName) {
        this.productName = productName;
    }

    @Step("Получение списка объекта продуктового каталога")
    public List<ItemImpl> getProductObjectList(Class<?> clazz) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get(productName)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    @Step("Получение Meta данных объекта продуктового каталога")
    public MetaImpl getMeta(Class<?> clazz) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get(productName)
                .assertStatus(200)
                .extractAs(clazz)).getMeta();
    }

    @Step("Создание объекта продуктового каталога")
    public Response createProductObject(JSONObject body) {
        return new Http(ProductCatalogURL)
                .body(body)
                .post(productName);
    }

    @Step("Получение списка объекта продуктового каталога используя multisearch")
    public List<ItemImpl> getProductObjectListWithMultiSearch(Class<?> clazz, String str) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?multisearch=" + str)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    @Step("Проверка существования объекта продуктового каталога по имени")
    public boolean isExists(String name) {
        return new Http(ProductCatalogURL)
                .get(productName + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Импорт объекта продуктового каталога")
    public void importObject(String pathName) {
        new Http(ProductCatalogURL)
                .multiPart(productName + "obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Получение объекта продуктового каталога по Id")
    public GetImpl getById(String objectId, Class<?> clazz) {
        return (GetImpl) new Http(ProductCatalogURL)
                .get(productName + objectId + "/")
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id и Env")
    public GetImpl getByIdAndEnv(String objectId, String env, Class<?> clazz) {
        return (GetImpl) new Http(ProductCatalogURL)
                .get(productName + objectId + "/?env={}", env)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id и по версии объекта")
    public GetImpl getByIdAndVersion(String objectId, String version, Class<?> clazz) {
        return (GetImpl) new Http(ProductCatalogURL)
                .get(productName + objectId + "/?version=" + version)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id без токена")
    public void getByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL).setWithoutToken()
                .get(productName + objectId + "/").assertStatus(401);
    }

    @Step("Обновление объекта продуктового каталога")
    public GetImpl patchObject(Class<?> clazz, String name, String graphId, String objectId) {
        return (GetImpl) new Http(ProductCatalogURL)
                .body(toJson("productCatalog/actions/createAction.json", name, graphId))
                .patch(productName + objectId + "/")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Копирование объекта продуктового каталога по Id")
    public void copyById(String objectId) {
        new Http(ProductCatalogURL)
                .post(productName + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Копирование объекта продуктового каталога по Id без ключа")
    public void copyByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL)
                .setWithoutToken()
                .post(productName + objectId + "/copy/")
                .assertStatus(401);
    }

    @Step("Экспорт объекта продуктового каталога по Id")
    public void exportById(String objectId) {
        new Http(ProductCatalogURL)
                .get(productName + objectId + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Удаление объекта продуктового каталога по имени")
    public void deleteByName(String name, Class<?> clazz) {
        deleteById(getProductObjectIdByNameWithMultiSearch(name, clazz));
    }

    @Step("Обновление всего объекта продуктового каталога по Id")
    public void putObjectById(String objectId, JSONObject body) {
        new Http(ProductCatalogURL)
                .body(body)
                .put(productName + objectId + "/")
                .assertStatus(200);
    }

    @Step("Удаление объекта продуктового каталога по Id")
    public void deleteById(String objectId) {
        getDeleteObjectResponse(objectId).assertStatus(204);
    }

    @Step("Удаление объекта продуктового каталога по Id без токена")
    public void deleteObjectByIdWithOutToken(String id) {
        new Http(ProductCatalogURL)
                .setWithoutToken()
                .delete(productName + id + "/").assertStatus(401);
    }

    @Step("Поиск ID объекта продуктового каталога по имени с использованием multiSearch")
    public String getProductObjectIdByNameWithMultiSearch(String name, Class<?> clazz) {
        String objectId = null;
        List<ItemImpl> list = ((GetListImpl) new Http(ProductCatalogURL)
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

    @Step("Поиск ID объекта продуктового каталога по Title")
    public String getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(String title, String parameters) {
        return Objects.requireNonNull(new Http(ProductCatalogURL)
                .get("{}?multisearch={}&{}", productName, title, parameters)
                .assertStatus(200)
                .jsonPath()
                .getString("list.find{it.title.toLowerCase()=='" + title.toLowerCase() + "'}.id"), "ID продукта: " + title + " не найден");
    }

    @Step("Обновление объекта продуктового каталога")
    public Response patchRow(JSONObject body, String actionId) {
        return new Http(ProductCatalogURL)
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
    public Response partialUpdateObject(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .body(object)
                .patch(productName + id + "/");
    }
    //todo "Получить сообщение, сравнить с ответом"
    @Step("Частичное обновление продукта без токена")
    public void partialUpdateObjectWithOutToken(String id, JSONObject object) {
        new Http(ProductCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(productName + id + "/")
                .assertStatus(401);
    }

    @Step("Получение времени отклика на запрос")
    public long getTime(String url) {
        io.restassured.response.Response response = given()
                .get(url);
        return response.getTime();
    }

    @Step("Получение массива объектов используещих граф")
    public JsonPath getObjectArrayUsedGraph(String id) {
        return new Http(ProductCatalogURL)
                .get("graphs/" + id + "/used/")
                .assertStatus(200).jsonPath();
    }

    public Response getDeleteObjectResponse(String id) {
        return new Http(ProductCatalogURL)
                .delete(productName + id + "/");
    }

    @Step("Получение списка объектов продуктового каталога по фильтру")
    public List<ItemImpl> getProductObjectList(Class<?> clazz, String filter) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .get(productName + filter)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    public JsonPath getJsonPath(String id) {
        return new Http(ProductCatalogURL)
                .get(productName + id + "/")
                .assertStatus(200).jsonPath();
    }

    public JsonPath getVersionJsonPath(String id) {
        return new Http(ProductCatalogURL)
                .get(productName + id + "/version_list/")
                .assertStatus(200).jsonPath();
    }

    @Step("Получение объекта продуктового каталога по имени")
    public GetListImpl getObjectListByName(String name, Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?name=" + name)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение списка объектов продуктового каталога по именам")
    public GetListImpl getObjectsListByNames(Class<?> clazz, String... name) {
        String names = String.join(",", name);
        return (GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?name__in=" + names)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по title")
    public GetListImpl getObjectByTitle(String title, Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?title=" + title)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по type")
    public GetListImpl getObjectListByType(String type, Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?type=" + type)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение info продукта")
    public Response getInfoProduct(String id) {
        return new Http(ProductCatalogURL)
                .get(productName + id + "/info/")
                .assertStatus(200);
    }

    @Step("Получение шаблона визуализации по event_type и event_provider")
    public GetVisualTemplateResponse getItemVisualTemplate(String eventType, String eventProvider) {
        return new Http(ProductCatalogURL)
                .get("item_visual_templates/item_visual_template/" + eventType + "/" + eventProvider + "/")
                .assertStatus(200)
                .extractAs(GetVisualTemplateResponse.class);
    }

    @Step("Сортировка объектов по дате создания")
    public GetListImpl orderingByCreateData(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по дате обновления")
    public GetListImpl orderingByUpDateData(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по статусу")
    public GetListImpl orderingByStatus(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .get(productName + "?ordering=status")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по имени с публичным токеном")
    public Response getObjectByNameWithPublicToken(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.VIEWER)
                .get(productName + "?name=" + name);
    }

    @Step("Создание объекта продуктового каталога с публичным токеном")
    public Response createProductObjectWithPublicToken(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.VIEWER)
                .body(body)
                .post(productName);
    }

    @Step("Обновление объекта продуктового каталога с публичным токеном")
    public Response partialUpdateObjectWithPublicToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.VIEWER)
                .body(object)
                .patch(productName + id + "/");
    }

    @Step("Удаление объекта продуктового каталога с публичным токеном")
    public Response deleteObjectWithPublicToken(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.VIEWER)
                .delete(productName + id + "/");
    }

    @Step("Обновление всего объекта продуктового каталога по Id с публичным токеном")
    public Response putObjectByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.VIEWER)
                .body(body)
                .put(productName + objectId + "/");
    }

    @Step("Проверка сортировки списка")
    public boolean isSorted(List<ItemImpl> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            String currentName = delNoDigOrLet(list.get(i).getName());
            String nextName = delNoDigOrLet(list.get(i + 1).getName());
            if (currentTime.isBefore(nextTime) || (currentTime.isEqual(nextTime) && currentName.compareToIgnoreCase(nextName) > 0)) {
                return false;
            }
        }
        return true;
    }

    @Step("Получение productOrgInfo по id product и организации")
    public CreateInfoSystemResponse getProductOrgInfoSystem(String productId, String orgName) {
        return new Http(ProductCatalogURL)
                .get(productName + productId + "/organizations/" + orgName + "/")
                .assertStatus(200)
                .extractAs(CreateInfoSystemResponse.class);
    }

    private static String delNoDigOrLet (String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character .isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
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
