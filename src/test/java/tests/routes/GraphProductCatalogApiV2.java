package tests.routes;

import core.helper.http.Path;

public class GraphProductCatalogApiV2 extends ProductCatalogApi {

    @Api.Route(method = Api.Method.GET, path = "/api/v2/graphs/{name}/input_vars/", status = 200)
    public static Path apiV2GraphsInputVars;
}
