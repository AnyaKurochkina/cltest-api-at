package steps.deleteAllOrders;


import core.helper.Configurier;
import core.helper.Http;
import core.helper.Templates;
import core.vars.LocalThead;
import core.vars.TestVars;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import io.qameta.allure.Step;

@Log4j2
public class DeleteAllOrders {

    private static final String URL = Configurier.getInstance().getAppProp("host_kong");
    String datafolder = Configurier.getInstance().getAppProp("data.folder");

    @Step("Удаление всех заказов")
    public void deleteOrders(String project_id) throws IOException, ParseException {
        Templates templates = new Templates();
        TestVars testVars = LocalThead.getTestVars();
        testVars.setVariables("project_id", project_id);
        String action = new String();
        List orders = new Http(URL)
                .get(String.format("order-service/api/v1/projects/%s/orders?include=total_count&page=1&per_page=100&f[category]=vm", project_id))
                .assertStatus(200)
                .jsonPath()
                .get("list.findAll{it.status == 'success'}.id");

        System.out.println("list = " + orders);

        for (int i = 0; i < orders.size(); i++) {
            String order_id = (String) orders.get(i);
            testVars.setVariables("order_id", order_id);
            System.out.println("order_id = " + order_id);
            String product_name = new Http(URL)
                    .get(String.format("order-service/api/v1/projects/%s/orders/%s", project_id, order_id))
                    .assertStatus(200)
                    .jsonPath()
                    .get("attrs.product_title");

            System.out.println("product_name = " + product_name);

            if ("postgresql".equals(product_name.toLowerCase()) || "rabbitmq".equals(product_name.toLowerCase()) || "nginx".equals(product_name.toLowerCase()) || "redis".equals(product_name.toLowerCase()) || "apache kafka".equals(product_name.toLowerCase())) {
                action = "delete_two_layer";
            } else {
                action = "delete_vm";
            }
            System.out.println("action = " + action);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(datafolder + "/actions/template.json"));

            JSONObject template = (JSONObject) obj;
            JSONObject request = templates.ChangeActionTemplate(template, action);
            JsonPath response = new Http(URL)
                    .patch(String.format("order-service/api/v1/projects/%s/orders/%s/actions/%s", project_id, order_id, action), request)
                    .assertStatus(200)
                    .jsonPath();
        }


    }
}
