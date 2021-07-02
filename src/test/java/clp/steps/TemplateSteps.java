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

    private static final Logger log = LoggerFactory.getLogger(OrderSteps.class);

    public static JSONObject getRequest(String product) throws IOException, ParseException {

        String datafolder = Configurier.getInstance().getAppProp("data.folder");
        org.json.simple.parser.JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(datafolder + "/orders/" + product.toLowerCase() + ".json"));
        JSONObject template =  (JSONObject) obj;
        /** Изменение шаблона запроса */
        JSONObject request = ChangeOrderTemplate(template, product);
        return request;
    }

    public static JSONObject ChangeOrderTemplate (JSONObject request, String product) throws IOException, ParseException {
        JsonHelper.getAllTestDataValues( "product" + ".json", product );
        log.info("Изменение базового шаблона запроса тестовыми данными: ");
        if (product.equals("Rhel") || product.equals("Windows") || product.equals("PostgreSQL") || product.equals("Redis") || product.equals("ApacheKafka")) {
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.project_name", testValues.get("project_name"));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.count", Integer.parseInt(testValues.get("count")));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.attrs.default_nic.net_segment", testValues.get("net_segment"));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.attrs.platform", testValues.get("platform"));
        }
        return request;
    }

    /*public static JSONObject getActionRequest(String order_id) throws IOException, ParseException {

        String datafolder = Configurier.getInstance().getAppProp("data.folder");
        org.json.simple.parser.JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(datafolder + "/actions/" + product.toLowerCase() + ".json"));
        JSONObject template =  (JSONObject) obj;
        JSONObject request = ChangeOrderTemplate(template, product);
        return request;
    }*/
}
