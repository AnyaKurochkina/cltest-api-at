package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.OrgDirection.existsOrgDirection.response.ExistsOrgDirectionResponse;
import httpModels.productCatalog.OrgDirection.exportOrgDirection.respose.ExportOrgDirectionResponse;
import httpModels.productCatalog.OrgDirection.getOrgDirection.response.GetOrgDirectionResponse;
import httpModels.productCatalog.OrgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import httpModels.productCatalog.OrgDirection.getOrgDirectionList.response.ListItem;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrgDirectionSteps {

    @Step("Получение списка направлений")
    public List<ListItem> getOrgDirectionList() {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/")
                .assertStatus(200)
                .extractAs(GetOrgDirectionListResponse.class)
                .getList();
    }

    @Step("Проверка существования направления")
    public boolean isProductExists(String name) {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/exists/?name=" + name)
                .assertStatus(200)
                .extractAs(ExistsOrgDirectionResponse.class)
                .getExists();
    }

    @Step("Ипорт направления")
    public void importOrgDirection(String pathName) {
        ValidatableResponse response = given()
                .contentType("multipart/form-data")
                .multiPart("file", new File(pathName))
                .when()
                .post("http://dev-kong-service.apps.d0-oscp.corp.dev.vtb/product-catalog/org_direction/obj_import/")
                .then()
                .statusCode(200);
    }

    @Step("Получение направления по Id")
    public GetOrgDirectionResponse getOrgDirectionById(String id) {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/" + id + "/")
                .assertStatus(200)
                .extractAs(GetOrgDirectionResponse.class);
    }

    @Step("Обновление направления по Id")
    public void updateOrgDirectionById(String id, JSONObject jsonObject) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .patch("org_direction/" + id + "/", jsonObject)
                .assertStatus(200);
    }

    @Step("Создание JSON объекта по направлениям")
    public JSONObject createJsonObject(String name, String description) {
        return new JsonHelper()
                .getJsonTemplate("productCatalog/orgDirection/orgDirection.json")
                .set("$.name", name)
                .set("$.description", description)
                .build();
    }

    @Step ("Удаление направления по Id")
    public void deleteOrgDirectoryById(String id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .delete("org_direction/" + id + "/")
                .assertStatus(204);
    }

    @Step ("Копирование направления по Id")
    public void copyOrgDirectionById(String id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .post("org_direction/" + id + "/copy/")
                .assertStatus(200);
    }

    @Step ("Экспорт направления по Id")
    public ExportOrgDirectionResponse exportOrgDirectionById(String id) {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/" + id + "/obj_export/")
                .assertStatus(200)
                .extractAs(ExportOrgDirectionResponse.class);
    }
}
