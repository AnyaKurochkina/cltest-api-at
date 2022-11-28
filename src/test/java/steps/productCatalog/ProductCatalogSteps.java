package steps.productCatalog;

import api.cloud.productCatalog.IProductCatalog;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.MetaImpl;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.product.getProducts.getProductsExportList.ExportItem;
import httpModels.productCatalog.product.getProducts.getProductsExportList.GetProductsExportList;
import httpModels.productCatalog.productOrgInfoSystem.createInfoSystem.CreateInfoSystemResponse;
import httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.GetInfoSystemListResponse;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static core.helper.Configure.ProductCatalogURL;

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
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    @Step("Получение версии продуктового каталога")
    public static Response getProductCatalogVersion() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/version/")
                .assertStatus(200);
    }

    @Step("Получение статуса health check")
    public static String getHealthCheckStatusProductCatalog() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/healthcheck/")
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    @Step("Получение статуса health")
    public static String getHealthStatusProductCatalog() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/health/")
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    @Step("Загрузка объекта в Gitlab")
    public Response dumpToBitbucket(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productName + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка объекта из Gitlab")
    public Response loadFromBitbucket(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(productName + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Получение Meta данных объекта продуктового каталога")
    public MetaImpl getMeta(Class<?> clazz) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName)
                .assertStatus(200)
                .extractAs(clazz)).getMeta();
    }

    @Step("Создание объекта продуктового каталога")
    public Response createProductObject(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(productName);
    }

    @Step("Получение списка объекта продуктового каталога используя multisearch")
    public List<ItemImpl> getProductObjectListWithMultiSearch(Class<?> clazz, String str) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?multisearch=" + str)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    @Step("Проверка существования объекта продуктового каталога по имени")
    public boolean isExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Импорт объекта продуктового каталога")
    public void importObject(String pathName) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(productName + "obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Получение объекта продуктового каталога по Id")
    public GetImpl getById(String objectId, Class<?> clazz) {
        return (GetImpl) new Http(ProductCatalogURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get(productName + objectId + "/")
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id и по версии объекта")
    public GetImpl getByIdAndVersion(String objectId, String version, Class<?> clazz) {
        return (GetImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(productName + objectId + "/?version=" + version)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id без токена")
    public void getByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL).setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + objectId + "/").assertStatus(401);
    }

    @Step("Обновление объекта продуктового каталога")
    public GetImpl patchObject(Class<?> clazz, String name, String graphId, String objectId) {
        return (GetImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(toJson("productCatalog/actions/createAction.json", name, graphId))
                .patch(productName + objectId + "/")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Копирование объекта продуктового каталога по Id")
    public void copyById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productName + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Копирование объекта продуктового каталога по Id без ключа")
    public void copyByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(productName + objectId + "/copy/")
                .assertStatus(401);
    }

    @Step("Экспорт объекта продуктового каталога по Id")
    public void exportById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
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
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
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
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?include=total_count&page=1&per_page=50&multisearch=" + name)
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

    @SneakyThrows
    @Step("Поиск ID объекта продуктового каталога по Title")
    public String getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(String title, String parameters) {
//        String productNameWithEncode = title.replaceAll("Разработка", "%D0%A0%D0%B0%D0%B7%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0");
        String productNameWithEncode = URLEncoder.encode(title, StandardCharsets.UTF_8.name());
        return Objects.requireNonNull(new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("{}?multisearch={}&{}", productName, productNameWithEncode, parameters)
                .assertStatus(200)
                .jsonPath()
                .getString("list.find{it.title.toLowerCase()=='" + title.toLowerCase() + "'}.id"), "ID продукта: " + title + " не найден");
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
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
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

    public Response getDeleteObjectResponse(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + id + "/");
    }

    @Step("Получение списка объектов продуктового каталога по фильтру")
    public List<ItemImpl> getProductObjectList(Class<?> clazz, String filter) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + filter)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    public JsonPath getJsonPath(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + id + "/")
                .assertStatus(200).jsonPath();
    }

    public JsonPath getVersionJsonPath(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + id + "/version_list/")
                .assertStatus(200).jsonPath();
    }

    @Step("Получение списка графов по Id")
    public List<httpModels.productCatalog.graphs.getGraphsList.response.ListItem> getGraphListById(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?id=" + id)
                .assertStatus(200)
                .extractAs(GetGraphsListResponse.class).getList();
    }

    @Step("Получение списка графов по Id")
    public Response getResponseGraphListById(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?id=" + id)
                .assertStatus(400);
    }

    @Step("Получение списка графов по нескольким Id")
    public List<httpModels.productCatalog.graphs.getGraphsList.response.ListItem> getGraphListByIds(String... id) {
        String ids = String.join(",", id);
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?id__in=" + ids)
                .assertStatus(200)
                .extractAs(GetGraphsListResponse.class).getList();
    }

    @Step("Получение списка графов по фильтру Id содержит")
    public List<httpModels.productCatalog.graphs.getGraphsList.response.ListItem> getGraphListByContainsId(String value) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?id__contains=" + value)
                .assertStatus(200)
                .extractAs(GetGraphsListResponse.class).getList();
    }

    @Step("Получение объекта продуктового каталога по имени")
    public GetListImpl getObjectListByName(String name, Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?name=" + name)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение списка объектов продуктового каталога по именам")
    public GetListImpl getObjectsListByNames(Class<?> clazz, String... name) {
        String names = String.join(",", name);
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?name__in=" + names)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по title")
    public GetListImpl getObjectByTitle(String title, Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?title=" + title)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по type")
    public GetListImpl getObjectListByType(String type, Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?type=" + type)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по дате создания")
    public GetListImpl orderingByCreateData(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по дате обновления")
    public GetListImpl orderingByUpDateData(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по статусу")
    public GetListImpl orderingByStatus(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productName + "?ordering=status")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по имени с публичным токеном")
    public Response getObjectByNameWithPublicToken(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(productName + "?name=" + name);
    }

    @Step("Создание объекта продуктового каталога с публичным токеном")
    public Response createProductObjectWithPublicToken(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .post(productName);
    }

    @Step("Обновление объекта продуктового каталога с публичным токеном")
    public Response partialUpdateObjectWithPublicToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(object)
                .patch(productName + id + "/");
    }

    @Step("Удаление объекта продуктового каталога с публичным токеном")
    public Response deleteObjectWithPublicToken(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .delete(productName + id + "/");
    }

    @Step("Обновление всего объекта продуктового каталога по Id с публичным токеном")
    public Response putObjectByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .put(productName + objectId + "/");
    }

    @Step("Проверка сортировки списка")
    public static boolean isSorted(List<? extends IProductCatalog> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
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
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/product_org_info_system/" + productId + "/organizations/" + orgName + "/")
                .assertStatus(200)
                .extractAs(CreateInfoSystemResponse.class);
    }

    @Step("Удаление productOrgInfo по id product и организации")
    public void deleteProductOrgInfoSystem(String productId, String orgName) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + productId + "/organizations/" + orgName + "/")
                .assertStatus(204);
    }

    @Step("Получение productOrgInfo по id product")
    public GetInfoSystemListResponse getProductOrgInfoSystemById(String productId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/product_org_info_system/" + productId + "/organizations/")
                .assertStatus(200)
                .extractAs(GetInfoSystemListResponse.class);
    }

    public static String delNoDigOrLet(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    public boolean isContains(List<ItemImpl> itemList, String name) {
        for (ItemImpl item : itemList) {
            if (item.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }



    public Response getProductByContextProject(String projectId, String productId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/{}/products/{}/", projectId, productId)
                .assertStatus(200);
    }

    @Step("Получение списка доступных категорий по id проекта")
    public List<String> getAvailableCategoriesByContextProject(String projectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/{}/products/categories/", projectId)
                .assertStatus(200)
                .jsonPath().getList("");
    }

    @Step("Получение доступных категорий")
    public List<String> getAvailableCategories() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/categories/")
                .assertStatus(200)
                .jsonPath().getList("");
    }

    public boolean isOrgContains(List<httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.ListItem> itemList, String orgName) {
        if (itemList.isEmpty()) {
            return true;
        }
        for (httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.ListItem item : itemList) {
            if (item.getOrganization().equals(orgName)) {
                return true;
            }
        }
        return false;

    }

    @Step("Получение файла экспорта списка продуктов")
    // TODO: 21.11.2022 сравнение с jsonshema
    public List<ExportItem> getProductsExportList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(" /api/v1/products/product_list_export/")
                .assertStatus(200)
                .extractAs(GetProductsExportList.class).getList();

    }
    @Step("Получение файла в формате {format} экспорта списка продуктов")
    public Response getProductsExportListInFormat(String format) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/product_list_export/?format={}", format)
                .assertStatus(200);
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
