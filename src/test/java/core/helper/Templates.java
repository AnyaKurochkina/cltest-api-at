package core.helper;

import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import stepsOld.OrderServiceSteps;

import java.io.FileReader;
import java.io.IOException;

import static core.helper.JsonHelper.shareData;
import static stepsOld.Steps.dataFolder;

@Log4j2
public class Templates {

    JsonHelper jsonHelper = new JsonHelper();
//    private static final Logger log = LoggerFactory.getLogger(TemplateSteps.class);

    public JSONObject getRequest(String product, String projectId) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(dataFolder + "/orders/" + product.toLowerCase() + ".json"));
        JSONObject template = (JSONObject) obj;
        /** Изменение шаблона запроса */
        return ChangeOrderTemplate(template, product, projectId);
    }

    public JSONObject ChangeOrderTemplate(JSONObject request, String product, String projectId) throws ParseException {
        jsonHelper.getAllTestDataValues("product" + ".json", product);
        log.info("Изменение базового шаблона запроса тестовыми данными: ");
        if (product.equals("Rhel") || product.equals("Windows") || product.equals("PostgreSQL") || product.equals("Redis") || product.equals("ApacheKafka") || product.equals("RabbitMQ") || product.equals("Nginx")) {
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.project_name", shareData.get(projectId));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.count", Integer.parseInt(jsonHelper.testValues.get("count")));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.attrs.default_nic.net_segment", jsonHelper.testValues.get("net_segment"));
            com.jayway.jsonpath.JsonPath.parse(request).set("$.order.attrs.platform", jsonHelper.testValues.get("platform"));
        }
        return request;
    }


    public JSONObject ChangeActionTemplate(JSONObject request, String action) throws IOException, ParseException {
        OrderServiceSteps orderSteps = new OrderServiceSteps();
        log.info("Заполнение тестовыми данными шаблона для выбранного экшена: ");
        if (!action.equals("test")) {
            com.jayway.jsonpath.JsonPath.parse(request).set("$.item_id", orderSteps.getItemIdByOrderId(action));
        }
        return request;
    }


    public JSONObject getJsonFromFile(String file) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(dataFolder + file));
            return (JSONObject) obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error(ex.getMessage());
        }
    }

}
