package steps.orderService;

import core.exception.CustomException;
import core.helper.*;
import core.utils.Waiting;
import core.vars.LocalThead;
import core.vars.TestVars;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import stepsOld.StateServiceSteps;
import stepsOld.Steps;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static core.helper.JsonHelper.shareData;

@Log4j2
//TODO: Актуализировать класс
public class OrderServiceSteps extends Steps {
    public static final String URL = Configurier.getInstance().getAppProp("host_kong");

   // @Step("Статус заказа - {status}")
    public void checkOrderStatus(String exp_status, IProduct product) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String orderStatus = "";
        int counter = 10;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(120000);

            orderStatus = new Http(URL)
                    .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                    .assertStatus(200)
                    .jsonPath()
                    .get("status");
            System.out.println("orderStatus = " + orderStatus);
            counter = counter - 1;
        }

        if (!orderStatus.equals(exp_status.toLowerCase())) {
            try {
                stateServiceSteps.GetErrorFromOrch(product.getOrderId());
            } catch (CustomException e) {
                e.printStackTrace();
            }
        }

    }



    @Step("Выполнить действие - {action}")
    public String executeAction(String action, IProduct product){
        String datafolder = Configurier.getInstance().getAppProp("data.folder");
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(datafolder + "/actions/template.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        Map<String,String> map = getItemIdByOrderId(action, product);
        JSONObject template = (JSONObject) obj;
        com.jayway.jsonpath.JsonPath.parse(template).set("$.item_id", map.get("item_id"));
        log.info("Отправка запроса на выполнение действия - " + action);

        JsonPath response = new Http(URL)
                .patch("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/" + map.get("name"), template)
                .assertStatus(200)
                .jsonPath();

        return response.get("action_id");
    }

    @Step("Статус выполнения последнего действия - {exp_status}")
    public void checkActionStatus(String exp_status, IProduct product, String action_id) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String action_status = "";
        int counter = 10;
        log.info("Проверка статуса выполнения действия");
        while ((action_status.equals("pending") || action_status.equals("")) && counter > 0) {
            Waiting.sleep(60000);
            try {
                action_status = new Http(URL)
                        .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/history/" + action_id)
                        .jsonPath().get("status");
            } catch (JsonPathException e) {
                log.error("Error get status " + e.getMessage());
            }
            ;
            counter = counter - 1;
        }
        if (!action_status.equals(exp_status.toLowerCase())) {
            try {
                stateServiceSteps.GetErrorFromOrch(product.getOrderId());
            } catch (CustomException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String,String> getItemIdByOrderId(String action, IProduct product) {
        log.info("Получение item_id для " + action);
        JsonPath jsonPath = new Http(URL)
                .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .jsonPath();

        Map<String,String> map = new HashMap<>();
        map.put("item_id", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.item_id", action)));
        map.put("name", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.actions.find{it.title.contains('%s')}.name", action, action)));
        return map;
    }

}
