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
import org.json.JSONObject;

import java.util.List;

import static core.helper.JsonHelper.convertResponseOnClass;

public class OrgDirectionSteps {

    @Step("Получение списка направлений")
    public List<ListItem> getOrgDirectionList() {
        String object = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/")
                .assertStatus(200)
                .toString();
        GetOrgDirectionListResponse response = convertResponseOnClass(object, GetOrgDirectionListResponse.class);
        return response.getList();
    }

    @Step("Проверка существования направления")
    public boolean isExist(String name) {
        String object = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/exists/?name=" + name)
                .assertStatus(200)
                .toString();
        ExistsOrgDirectionResponse response = convertResponseOnClass(object, ExistsOrgDirectionResponse.class);
        return response.getExists();
    }

    @Step("Ипорт направления")
    public void importOrgDirection(JSONObject jsonObject) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .post("org_direction/obj_import/", jsonObject)
                .assertStatus(201);
    }

    @Step("Получение направлеия по Id")
    public GetOrgDirectionResponse getOrgDirectionById(String id) {
        String object = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/" + id + "/")
                .assertStatus(200)
                .toString();
        return convertResponseOnClass(object, GetOrgDirectionResponse.class);
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
        String object = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("org_direction/" + id + "/obj_export/")
                .assertStatus(200)
                .toString();
        return convertResponseOnClass(object, ExportOrgDirectionResponse.class);
    }
}
