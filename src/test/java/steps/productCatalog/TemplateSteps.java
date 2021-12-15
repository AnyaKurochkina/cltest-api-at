package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Template.existsTemplate.response.ExistsTemplateResponse;
import httpModels.productCatalog.Template.getListTemplate.response.GetTemplateListResponse;
import httpModels.productCatalog.Template.getListTemplate.response.ListItem;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.util.List;

import static core.helper.JsonHelper.convertResponseOnClass;

@Log4j2
public class TemplateSteps {

    @Step("Получение списка шиблонов")
    public List<ListItem> getTemplateList() {
        String object = new Http(Configure.ProductCatalogURL)
                
                .get("templates/")
                .assertStatus(200).toString();
        GetTemplateListResponse getTemplateListResponse = convertResponseOnClass(object, GetTemplateListResponse.class);
        return getTemplateListResponse.getList();
    }

    @SneakyThrows
    @Step("Проверка на существование шаблона по имени")
    public boolean isExist(String name) {
        String object = new Http(Configure.ProductCatalogURL)
                
                .get("templates/exists/?name=" + name)
                .assertStatus(200)
                .toString();
        ExistsTemplateResponse response = convertResponseOnClass(object, ExistsTemplateResponse.class);
        return response.getExists();
    }

    @Step("Получение шаблона по Id")
    public void getTemplateById(Integer id) {
        new Http(Configure.ProductCatalogURL)
                
                .setWithoutToken()
                .get("templates/" + id + "/")
                .assertStatus(200);
    }

    @Step("Копирование шаблона по ID")
    public void copyTemplateById(Integer id) {
        new Http(Configure.ProductCatalogURL)
                
                .setWithoutToken()
                .post("templates/" + id + "/copy/")
                .assertStatus(200);
    }

    @Step("Удаление клона шаблона по имени")
    public void deleteTemplateByName(String name) {
        Integer id = getTemplateIdByNameMultiSearch(name);
        new Http(Configure.ProductCatalogURL)
                
                .setWithoutToken()
                .delete("templates/" + id + "/")
                .assertStatus(204);
    }

    @Step("Обновление параметра color у шаблона.")
    public void updateTemplateById(String color, String name, Integer id) {
        new Http(Configure.ProductCatalogURL)
                
                .setWithoutToken()
                .body(JsonHelper
                        .getJsonTemplate("productCatalog/templates/createTemplate.json")
                        .set("$.name", name)
                        .set("$.color", color)
                        .build())
                .patch("templates/" + id + "/");
    }

    @Step("Получение ID шаблона по его имени: {templateName} с использованием multiSearch")
    public Integer getTemplateIdByNameMultiSearch(String templateName) {
        GetTemplateListResponse response = new Http(Configure.ProductCatalogURL)
                
                .setWithoutToken()
                .get("templates/?multisearch=" + templateName)
                .assertStatus(200)
                .extractAs(GetTemplateListResponse.class);

        if (response.getList().size() == 0) {
            log.info("ID не найден, будет возвращён NULL");
            return null;
        } else {
            return response.getList().get(0).getId();
        }
    }

    @Step("Создание JSON объекта по шаблонам")
    public JSONObject createJsonObject(String name) {
        return new JsonHelper()
                .getJsonTemplate("productCatalog/templates/createTemplate.json")
                .set("$.name", name)
                .build();
    }

    @SneakyThrows
    @Step("Создание шаблона")
    public Http.Response createProduct(JSONObject body) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .post("templates/");
    }
}
