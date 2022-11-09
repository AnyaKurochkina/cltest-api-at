package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class TemplateSteps extends Steps  {

    private static final String templateUrl = "/api/v1/templates/";

    @Step("Создание шаблона")
    public static Response createTemplate(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(templateUrl);
    }

    @Step("Полуение списка узлов использующих шаблон")
    public static Response getNodeListUsedTemplate(Integer id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(templateUrl + "{}/used/", id)
                .compareWithJsonSchema("jsonSchema/template/getNodesUsedTemplateSchema.json")
                .assertStatus(200);
    }
}
