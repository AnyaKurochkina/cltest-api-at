package steps.orderService;

import com.google.gson.JsonArray;
import core.exception.CustomException;
import core.helper.*;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.authorizer.InformationSystem;
import models.orderService.interfaces.IProduct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import steps.Steps;
import stepsOld.StateServiceSteps;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class OrderServiceSteps extends Steps {
    public static final String URL = Configurier.getInstance().getAppProp("host_kong");

   // @Step("Статус заказа - {status}")
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
    public String executeAction(String action, IProduct product){
        Map<String,String> map = getItemIdByOrderId(action, product);
        log.info("Отправка запроса на выполнение действия - " + action);
        JsonPath response = jsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", map.get("item_id"))
                .send(OrderServiceSteps.URL)
                .setProjectId(product.getProjectId())
                .patch("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/" + map.get("name"))
                .assertStatus(200)
                .jsonPath();

        return response.get("action_id");
    }

    @Step("Выполнить действие - {action}")
    public String executeAction(String action, String dataString, IProduct product) {
        JSONParser parser = new JSONParser();
        Object data = null;
        try {
            data = parser.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String,String> map = getItemIdByOrderId(action, product);
        log.info("Отправка запроса на выполнение действия - " + action);
        JsonPath response = jsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", map.get("item_id"))
                .set("$.order.data", data)
                .send(OrderServiceSteps.URL)
                .setProjectId(product.getProjectId())
                .patch("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/" + map.get("name"))
                .assertStatus(200)
                .jsonPath();

        return response.get("action_id");
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

    /*@Test
    public void test(){
        String URL = "https://ift-kong-service.apps.d0-oscp.corp.dev.vtb/";
        JsonPath jsonPath  = new Http(URL)
                .setProjectId("proj-xazpppulba")
                .get("references/api/v1/pages/?directory__name=flavors&tags=" + "c422069e-8f01-4328-b9dc-4a9e5dafd44e")
                .assertStatus(200)
                .jsonPath();

        System.out.println("get resp " + jsonPath);
        JSONObject obj = jsonPath.get("[1]");
        System.out.println("get resp num 1 " + obj);
    }*/

    public Map<String,String> getFlavorByProduct(IProduct product) {
        log.info("Получение id продукта для продукта " + product.getProductName());
        InformationSystem informationSystem = cacheService.entity(InformationSystem.class).getEntity();
        String product_id = "";
        int total_count = new Http(URL)
                .setProjectId(product.getProjectId())
                .get(String.format("product-catalog/products/?is_open=true&env=%s&information_systems=%s&page=1&per_page=100", product.getEnv().toLowerCase(), informationSystem.id))
                .assertStatus(200)
                .jsonPath()
                .get("meta.total_count");

        int countOfIteration = total_count/ 100 + 1;
        for (int i = 1; i<=countOfIteration; i++) {
            product_id = new Http(URL)
                    .get(String.format("product-catalog/products/?is_open=true&env=%s&information_systems=%s&page=%s&per_page=100", product.getEnv().toLowerCase(), informationSystem.id, i))
                    .assertStatus(200)
                    .jsonPath()
                    .get(String.format("list.find{it.title.contains('%s') || it.title.contains('%s')}.id", product.getProductName().toLowerCase(), product.getProductName().toUpperCase()));
            if(product_id != null)
                log.info("Id продукта = " + product_id);
                break;
        }

        JsonPath jsonPath  = new Http(URL)
                .get("references/api/v1/pages/?directory__name=flavors&tags=" + product_id)
                .assertStatus(200)
                .jsonPath();


        /*JSONArray jsonArray  = new Http(URL)
                .get("references/api/v1/pages/?directory__name=flavors&tags=" + product_id)
                .assertStatus(200)
                .toJsonArray();

        int i = jsonArray.get()*/

        String flavor = String.format("{\"flavor\":{\"cpus\":%s,\"name\":\"%s\",\"uuid\":\"%s\",\"memory\":%s}}", jsonPath.get("[1].data.cpus"), jsonPath.get("[1].name"), jsonPath.get("[1].id"), jsonPath.get("[1].data.memory"));
        Map<String,String> map = new HashMap<>();
        map.put("cpus", jsonPath.get("data.cpus"));
        map.put("memory", jsonPath.get("data.memory"));
        map.put("flavor", flavor);
        return map;
    }

    public Map<String,String> getItemIdByOrderId(String action, IProduct product) {
        log.info("Получение item_id для " + action);
        JsonPath jsonPath = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .jsonPath();

        Map<String,String> map = new HashMap<>();
        map.put("item_id", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.item_id", action)));
        map.put("name", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.actions.find{it.title.contains('%s')}.name", action, action)));
        return map;
    }

    public int getExpandMountSize(IProduct product) {
        int size;
        log.info("Получение количества точек монтирования");
        size = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .assertStatus(200)
                .jsonPath()
                .get("data.find{it.type=='vm'}.config.extra_disks.size()");
        log.info(String.format("Количество дисков %s", size));
        return size;
    }
}
