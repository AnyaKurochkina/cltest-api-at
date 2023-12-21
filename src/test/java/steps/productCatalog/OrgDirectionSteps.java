package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.orgDirection.GetOrgDirectionList;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrgDirectionSteps extends Steps {
    private static final String orgDirUrl = "/api/v1/org_direction/";
    private static final String orgDirUrl2 = "/api/v2/org_direction/";

    @Step("Получение списка сервисов использующих направление")
    public static Response getServiceUsedOrgDirection(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl + "{}/used/", id)
                .compareWithJsonSchema("jsonSchema/orgDirection/getUsedServiceListSchema.json")
                .assertStatus(200);
    }

    @Step("Получение списка направлений")
    //todo сравнение с jsonShema
    public static List<OrgDirection> getOrgDirectionList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl)
                .assertStatus(200)
                .extractAs(GetOrgDirectionList.class).getList();
    }

    public static GetOrgDirectionList getOrgDirectionsList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl)
                .assertStatus(200)
                .extractAs(GetOrgDirectionList.class);
    }

    @Step("Проверка существования направления по имени")
    public static boolean isOrgDirectionExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение направления по имени")
    public static OrgDirection getOrgDirectionByName(String name) {
        List<OrgDirection> list = new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(orgDirUrl + "?{}", "name=" + name)
                .extractAs(GetOrgDirectionList.class).getList();
        assertEquals(name, list.get(0).getName());
        return list.get(0);
    }

    @Step("Удаление направления по id")
    public static void deleteOrgDirectionById(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(orgDirUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление направления по name {name}")
    public static void deleteOrgDirectionByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(orgDirUrl2 + name + "/")
                .assertStatus(204);
    }

    @Step("Создание направления")
    public static Response createOrgDirection(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(orgDirUrl);
    }

    @Step("Создание направления")
    public static OrgDirection createOrgDirectionByName(String name) {
        return OrgDirection.builder()
                .name(name)
                .build()
                .createObject();
    }

    @Step("Импорт направления")
    public static ImportObject importOrgDirection(String pathName) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(orgDirUrl + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Получение направления по Id {objectId}")
    public static OrgDirection getOrgDirectionById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl + objectId + "/")
                .extractAs(OrgDirection.class);
    }

    @Step("Получение направления по name {name}")
    public static OrgDirection getOrgDirectionByNameV2(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl2 + name + "/")
                .extractAs(OrgDirection.class);
    }

    @Step("Экспорт направления по Id")
    public static Response exportOrgDirectionById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl + objectId + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Частичное обновление направления")
    public static void partialUpdateOrgDirection(String id, JSONObject object) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(orgDirUrl + id + "/");
    }

    @Step("Копирование направления по Id")
    public static void copyOrgDirection(String objectId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(orgDirUrl + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Сортировка направлений по дате создания")
    public static boolean orderingOrgDirectionByCreateData() {
        List<OrgDirection> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(GetOrgDirectionList.class).getList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
            if (!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
                return false;
            }
        }
        return true;
    }

    @Step("Сортировка направлений по дате создания")
    public static boolean orderingOrgDirectionByUpdateData() {
        List<OrgDirection> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(GetOrgDirectionList.class).getList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpdateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpdateDt());
            if (!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
                return false;
            }
        }
        return true;
    }
}
