package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.getTemplate.response.GetTemplateResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;

import static core.helper.JsonHelper.convertResponseOnClass;

@Log4j2
public class TemplateSteps {

    @Step("Получение списка шиблонов")
    public void getTemplateList() {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("templates/")
                .assertStatus(200);
    }

    @Step("Проверка на существование шаблона")
    public void existTemplateByName(String templateName) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .setWithoutToken()
                .get("templates/exists/?name=" + templateName + "/")
                .assertStatus(200);
    }

    @Step("Получение шаблона по Id")
    public void getTemplateById(Integer id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .setWithoutToken()
                .get("templates/" + id + "/")
                .assertStatus(200);
    }

    @Step("Копирование шаблона по ID")
    public void copyTemplateById(Integer id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .setWithoutToken()
                .post("templates/" + id + "/copy/")
                .assertStatus(200);
    }

    @Step("Удаление клона шаблона по имени")
    public void deleteTemplateByName(String name) {
        Integer id = getTemplateIdByNameMultiSearch(name);
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .setWithoutToken()
                .delete("templates/" + id + "/")
                .assertStatus(204);
    }

    @Step("Обновление параметра color у шаблона.")
    public void updateTemplateById(String color, String name, Integer id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .setWithoutToken()
                .patch("templates/" + id + "/", new JsonHelper()
                        .getJsonTemplate("productCatalog/templates/createTemplate.json")
                        .set("$.name", name)
                        .set("$.color", color)
                        .build());
    }

    @Step("Получение ID шаблона по его имени: {templateName} с использованием multiSearch")
    public Integer getTemplateIdByNameMultiSearch(String templateName) {
        String object = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .setWithoutToken()
                .get("templates/?multisearch=" + templateName)
                .assertStatus(200)
                .toString();

        GetTemplateResponse response = convertResponseOnClass(object, GetTemplateResponse.class);
        if (response.getList().size() == 0) {
            log.info("ID не найден, будет возвращён NULL");
            return null;
        } else {
            return response.getList().get(0).getId();
        }
    }
}
