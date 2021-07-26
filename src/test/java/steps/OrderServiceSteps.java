package steps;

import core.exception.CustomException;
import core.helper.Configurier;
import core.helper.Http;
import core.helper.ShareData;
import core.helper.Templates;
import core.utils.Waiting;
import core.vars.LocalThead;
import core.vars.TestVars;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

import static core.helper.JsonHelper.shareData;

@Log4j2
public class OrderServiceSteps extends Steps{
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Заказ продукта {product} в проекте {projectId}")
    public void CreateOrder(String product, String projectId) throws IOException, ParseException {
        Templates templates = new Templates();
        TestVars testVars = LocalThead.getTestVars();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(dataFolder + "/orders/" + product.toLowerCase() + ".json"));
        JSONObject template = (JSONObject) obj;
        JSONObject request = templates.ChangeOrderTemplate(template, product, projectId);
        String order_id = "";
        log.info("Отправка запроса на создание заказа для " + product);

        JSONArray res = new Http(URL)
                .post("order-service/api/v1/projects/" + shareData.get(projectId) + "/orders", request)
                .assertStatus(201)
                .toJsonArray();

        order_id = (String) ((JSONObject) res.get(0)).get("id");
        testVars.setVariables("order_id", order_id);
        testVars.setVariables("project_id", shareData.get(projectId));
    }

    @Step("Заказ продукта {product} в среде {env}, сегмент {segment}, дата-центр {dataCentre}, платформа {platform}")
    public void CreateOrderWithOutline(String product, String env, String segment, String dataCentre, String platform) throws IOException, ParseException {
        Templates templates = new Templates();
        TestVars testVars = LocalThead.getTestVars();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(dataFolder + "/orders/" + product.toLowerCase() + ".json"));
        JSONObject template = (JSONObject) obj;
        //JSONObject request = templates.ChangeOrderTemplate(template, product, projectId);
        String order_id = "";
        log.info("Отправка запроса на создание заказа для " + product);
        String projectId = ShareData.get((String.format("projects.find{it.env == '%s'}.id",env)));
        JSONArray res = jsonHelper.getJsonTemplate("/orders/" + product.toLowerCase() + ".json")
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.project_name", projectId)
                .send(URL)
                .post("order-service/api/v1/projects/" + projectId + "/orders")
                .assertStatus(201)
                .toJsonArray();


        order_id = (String) ((JSONObject) res.get(0)).get("id");
        testVars.setVariables("order_id", order_id);
        testVars.setVariables("project_id", projectId);
    }

    @Step("Статус заказа - {status}")
    public void CheckOrderStatus(String exp_status) throws CustomException {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        TestVars testVars = LocalThead.getTestVars();
        String order_id = testVars.getVariable("order_id");
        String orderStatus = new String();
        int counter = 10;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(120000);

            orderStatus = new Http(URL)
                    .get("order-service/api/v1/projects/" + testVars.getVariable("project_id") + "/orders/" + order_id)
                    .assertStatus(200)
                    .jsonPath()
                    .get("status");
            System.out.println("orderStatus = " + orderStatus);
            counter = counter - 1;
        }

        if (!orderStatus.equals(exp_status.toLowerCase())) {
            stateServiceSteps.GetErrorFromOrch(order_id);
        }

    }

    @Step("Выполнить действие - {action}")
    public void ExecuteAction(String action) throws IOException, ParseException {
        Templates templates = new Templates();
        TestVars testVars = LocalThead.getTestVars();
        String datafolder = Configurier.getInstance().getAppProp("data.folder");
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(datafolder + "/actions/template.json"));

        JSONObject template = (JSONObject) obj;
        JSONObject request = templates.ChangeActionTemplate(template, action);
        log.info("Отправка запроса на выполнение действия - " + action);

        JsonPath response = new Http(URL)
                .patch("order-service/api/v1/projects/" + testVars.getVariable("project_id") + "/orders/" + testVars.getVariable("order_id") + "/actions/" + action, request)
                .assertStatus(200)
                .jsonPath();

        testVars.setVariables("action_id", response.get("action_id"));

    }

    @Step("Статус выполнения последнего действия - {exp_status}")
    public void CheckActionStatus(String exp_status) throws CustomException {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        TestVars testVars = LocalThead.getTestVars();
        String order_id = testVars.getVariable("order_id");
        String action_id = testVars.getVariable("action_id");
        String action_status = "";
        int counter = 10;

        log.info("Проверка статуса выполнения действия");
        while ((action_status.equals("pending") || action_status.equals("")) && counter > 0) {
            Waiting.sleep(60000);
            try {
                action_status = new Http(URL)
                        .get("order-service/api/v1/projects/" + testVars.getVariable("project_id") + "/orders/" + order_id + "/actions/history/" + action_id)
                        .jsonPath().get("status");
            } catch (JsonPathException e) {
                log.error("Error get status " + e.getMessage());
            }
            ;
            counter = counter - 1;
        }

        if (!action_status.equals(exp_status.toLowerCase())) {
            stateServiceSteps.GetErrorFromOrch(order_id);
        }
    }

    public String getItemIdByOrderId(String action) {
        TestVars testVars = LocalThead.getTestVars();
        String order_id = testVars.getVariable("order_id");
        log.info("Получение item_id для " + action);

        return new Http(URL)
                .get("order-service/api/v1/projects/" + testVars.getVariable("project_id") + "/orders/" + order_id)
                .jsonPath()
                .get(String.format("data.find{it.actions.find{it.name == '%s'}}.item_id", action));
    }

}
