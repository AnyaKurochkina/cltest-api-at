package steps.productCatalog;

import core.helper.Http;

public class GraphSteps {

    public void createGraph(){
        String object = new Http("http://d4-product-catalog.apps.d0-oscp.corp.dev.vtb/")
                .setContentType("application/json")
                .setWithoutToken()
                .post("graphs/?save_as_next_version=true")
                .assertStatus(200)
                .toString();
    }
}
