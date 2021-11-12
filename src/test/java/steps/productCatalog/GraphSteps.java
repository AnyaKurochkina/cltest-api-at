package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.getGraphs.response.GetGraphsResponse;
import httpModels.productCatalog.getGraphs.response.ListItem;
import org.json.JSONObject;

import static core.helper.JsonHelper.convertResponseOnClass;

public class GraphSteps {

    private JSONObject toJson(String pathToJsonBody) {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(pathToJsonBody).build();
    }

    public void createGraph() {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("graphs/?save_as_next_version=true", toJson("/productCatalog/graphs/createGraph.json"))
                .assertStatus(200)
                .toString();
    }

    public String getGraph(String graphName){
        String graphId = null;
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("graphs/?include=total_count&page=1&per_page=10")
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
