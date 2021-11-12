package steps.productCatalog;

import core.helper.Http;
import core.helper.JsonHelper;
import models.productCatalog.getGraphs.response.GetGraphsResponse;
import models.productCatalog.getGraphs.response.ListItem;
import org.json.JSONObject;

import static steps.productCatalog.ActionsSteps.convertResponseOnClass;

public class GraphSteps {

    public JSONObject toJson(String pathToJsonBody) {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(pathToJsonBody).build();
    }

    public void createGraph() {
        String object = new Http("http://d4-product-catalog.apps.d0-oscp.corp.dev.vtb/")
                .setContentType("application/json")
                .setWithoutToken()
                .post("graphs/?save_as_next_version=true", toJson("/productCatalog/graphs/createGraph.json"))
                .assertStatus(200)
                .toString();
    }

    public String getGraph(String graphName){
        String graphId = null;
        String object = new Http("http://d4-product-catalog.apps.d0-oscp.corp.dev.vtb/")
                .setContentType("application/json")
                .setWithoutToken()
                .get("graphs")
                .assertStatus(200)
                .toString();

        GetGraphsResponse response = convertResponseOnClass(object, GetGraphsResponse.class);

        for(ListItem listItem: response.getList()){
            if (listItem.getName().equals(graphName)){
                graphId = listItem.getId();
            }
        }
        return graphId;
    }
}
