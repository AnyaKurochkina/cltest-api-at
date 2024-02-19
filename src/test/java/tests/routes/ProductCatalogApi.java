package tests.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;

public class ProductCatalogApi implements Api {
    @Override
    public String url() {
        return KONG_URL + "product-catalog";
    }

    @Route(method = Method.POST, path = "/api/v1/check_item_restrictions/", status = 200)
    public static Path apiV1CheckItemRestrictionsCreate;
}
