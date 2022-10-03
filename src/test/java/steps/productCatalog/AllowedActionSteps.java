package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class AllowedActionSteps extends Steps {
    private static final String allowedUrl = "/api/v1/allowed_actions/";

    @Step("Проверка существования разрешенного действия по имени {name}")
    public static boolean isAllowedActionExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(allowedUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

}
