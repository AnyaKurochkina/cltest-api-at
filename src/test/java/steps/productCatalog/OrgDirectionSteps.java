package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class OrgDirectionSteps extends Steps {
    private static final String orgDirUrl = "/api/v1/org_direction/";

    @Step("Получение списка сервисов использующих направление")
    public static Response getServiceUsedOrgDirection(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(orgDirUrl + "{}/used/", id)
                .assertStatus(200);
    }
}
