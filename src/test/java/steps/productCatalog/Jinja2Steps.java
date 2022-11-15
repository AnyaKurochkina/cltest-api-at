package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.cloud.productCatalog.jinja2.GetJinja2List;
import models.cloud.productCatalog.jinja2.Jinja2;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class Jinja2Steps extends Steps {
    private static final String jinjaUrl = "/api/v1/jinja2_templates/";

    @Step("Получение списка jinja2")
    public static List<Jinja2> getJinja2List() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl)
                .compareWithJsonSchema("jsonSchema/getActionListSchema.json")
                .assertStatus(200)
                .extractAs(GetJinja2List.class).getList();
    }
}
