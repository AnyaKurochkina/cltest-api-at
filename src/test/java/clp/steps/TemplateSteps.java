package clp.steps;

import clp.core.helpers.Configurier;
import clp.core.helpers.JsonHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;

import static clp.core.helpers.JsonHelper.testValues;


public class TemplateSteps {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceSteps.class);
    private static final String datafolder = Configurier.getInstance().getAppProp("data.folder");

    public static JSONObject getRequest(String product) throws IOException, ParseException {
        org.json.simple.parser.JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(datafolder + "/orders/" + product.toLowerCase() + ".json"));
        JSONObject template = (JSONObject) obj;
        /** Изменение шаблона запроса */
        JSONObject request = ChangeOrderTemplate(template, product);
        return request;
    }

    public static JSONObject ChangeOrderTemplate(JSONObject request, String product) throws ParseException {
        JsonHelper.getAllTestDataValues("product" + ".json", product);
        log.info("Изменение базового шаблона запроса тестовыми данными: ");
        if (product.equals("Rhel") || product.equals("Windows") || product.equals("PostgreSQL") || product.equals("Redis") || product.equals("ApacheKafka") || product.equals("RabbitMQ") || product.equals("Nginx")) {
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.project_name", testValues.get("project_name"));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.count", Integer.parseInt(testValues.get("count")));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.attrs.default_nic.net_segment", testValues.get("net_segment"));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.attrs.platform", testValues.get("platform"));
        }
        return request;
    }

    public static JSONObject getActionRequest(String action) throws IOException, ParseException {
        String datafolder = Configurier.getInstance().getAppProp("data.folder");
        org.json.simple.parser.JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(datafolder + "/actions/" + action.toLowerCase() + ".json"));

        JSONObject template = (JSONObject) obj;
        JSONObject request = ChangeActionTemplate(template, action);

        return request;

    }

    public static JSONObject ChangeActionTemplate(JSONObject request, String action) throws IOException, ParseException {
        OrderServiceSteps orderSteps = new OrderServiceSteps();
        log.info("Заполнение тестовыми данными шаблона для выбранного экшена: ");
        if (!action.equals("test")) {
            com.jayway.jsonpath.JsonPath.parse(request).set("$.item_id", orderSteps.getItemIdByOrderId(action));
        }
        return request;
    }
}
