package api.routes;

import static core.helper.Configure.KONG_URL;

public class ProductCatalogApi implements Api {
    @Override
    public String url() {
        return KONG_URL + "product-catalog";
    }
}
