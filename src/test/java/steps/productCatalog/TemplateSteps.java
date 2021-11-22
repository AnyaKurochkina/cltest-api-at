package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import httpModels.productCatalog.getTemplate.response.GetTemplateResponse;
import httpModels.productCatalog.getTemplate.response.ListItem;
import io.qameta.allure.Step;

import static core.helper.JsonHelper.convertResponseOnClass;

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
}
