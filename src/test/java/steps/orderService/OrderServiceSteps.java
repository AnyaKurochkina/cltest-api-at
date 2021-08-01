package steps.orderService;

import core.exception.CustomException;
import core.helper.*;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import steps.Steps;
import stepsOld.StateServiceSteps;


import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class OrderServiceSteps extends Steps {
    public static final String URL = Configurier.getInstance().getAppProp("host_kong");

    public void checkOrderStatus(String exp_status, IProduct product) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String orderStatus = "";
        int counter = 40;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(30000);

            orderStatus = new Http(URL)
                    .setProjectId(product.getProjectId())
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
    public String executeAction(String action, IProduct product) {
        Map<String, String> map = getItemIdByOrderId(action, product);
        return jsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", map.get("item_id"))
                .send(URL)
                .setProjectId(product.getProjectId())
                .patch("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/" + map.get("name"))
                .assertStatus(200)
                .jsonPath()
                .get("action_id");
    }

    @Step("Выполнить действие с блоком data - {action}")
    public String executeAction(String action, String dataString, IProduct product) {
        Map<String, String> map = getItemIdByOrderId(action, product);
        return jsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", map.get("item_id"))
                .set("$.order.data", new JSONObject(dataString))
                .send(URL)
                .setProjectId(product.getProjectId())
                .patch("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/" + map.get("name"))
                .assertStatus(200)
                .jsonPath()
                .get("action_id");
    }

    @Step("Статус выполнения последнего действия - {exp_status}")
    public void checkActionStatus(String exp_status, IProduct product, String action_id) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String action_status = "";
        int counter = 20;
        log.info("Проверка статуса выполнения действия");
        while ((action_status.equals("pending") || action_status.equals("")) && counter > 0) {
            Waiting.sleep(30000);
            try {
                action_status = new Http(URL)
                        .setProjectId(product.getProjectId())
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

    public Map<String, String> getItemIdByOrderId(String action, IProduct product) {
        log.info("Получение item_id для " + action);
        JsonPath jsonPath = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .jsonPath();

        Map<String, String> map = new HashMap<>();
        map.put("item_id", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.item_id", action)));
        map.put("name", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.actions.find{it.title.contains('%s')}.name", action, action)));
        return map;
    }

    @Step("Получение списка ресурсных пулов для категории {category} и среды {env}")
    public void getResourcesPool(String category, String env) {
        Project project = cacheService.entity(Project.class)
                .setField("env", env)
                .getEntity();
        JSONObject jsonObject = new Http(URL)
                .setProjectId(project.id)
                .get(String.format("order-service/api/v1/products/resource_pools?category=%s&project_name=%s", category, project.id))
                .assertStatus(200)
                .toJson();
        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
        for(Object object : jsonArray){
            JSONObject j = (JSONObject) object;
            ResourcePool resourcePool = ResourcePool.builder()
                    .id(j.getString("id"))
                    .label(j.getString("label"))
                    .projectId(project.id)
                    .build();
            cacheService.saveEntity(resourcePool);
        }


    }

    public Comparable getFiledProduct(IProduct product, String path) {
        Comparable s;
        log.info("getFiledProduct path: " + path);
        JsonPath jsonPath = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .assertStatus(200)
                .jsonPath();
        s = jsonPath.get(path);
        log.info(String.format("getFiledProduct return: %s", s));
        Assert.assertNotNull("По path '" + path + "' не найден объект в response " + jsonPath.prettify(), s);
        return s;
    }
}
