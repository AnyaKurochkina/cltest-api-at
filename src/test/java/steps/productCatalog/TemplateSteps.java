package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import httpModels.productCatalog.getTemplate.response.GetTemplateResponse;
import httpModels.productCatalog.getTemplate.response.ListItem;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;

import static core.helper.JsonHelper.convertResponseOnClass;

@Log4j2
public class TemplateSteps {

    @Step("Получение ID шаблона по его имени: {templateName}")
    public Integer getTemplateIdByName(String templateName) {
        Integer templateId = null;
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("templates/")
                .assertStatus(200)
                .toString();

        GetTemplateResponse response = convertResponseOnClass(object, GetTemplateResponse.class);

        for (ListItem listItem : response.getList()) {
            if (listItem.getName().equals(templateName)) {
                templateId = listItem.getId();
                break;
            }
        }
        return templateId;
    }

    @Step("Получение ID шаблона по его имени: {templateName} с использованием multiSearch")
    public Integer getTemplateIdByNameMultiSearch(String templateName) {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
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
