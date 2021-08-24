package steps.orderService;

import core.exception.CustomException;
import core.helper.*;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.authorizer.InformationSystem;
import models.orderService.interfaces.IProduct;
import models.authorizer.Project;
import models.orderService.ResourcePool;
import models.orderService.interfaces.ProductStatus;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Assume;
import steps.Steps;
import steps.stateService.StateServiceSteps;
import steps.tarifficator.CostSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

@Log4j2
public class OrderServiceSteps extends Steps {
    public static final String URL = Configure.getAppProp("host_kong");

    public void checkOrderStatus(String exp_status, IProduct product) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String orderStatus = "";
        int counter = 40;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(30000);


//            orderStatus = new Http(URL)
//                    .setProjectId(product.getProjectId())
//                    .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
//                    .assertStatus(200)
//                    .jsonPath()
//                    .get("status");

            Http.HttpResponse res = new Http(URL)
                    .setProjectId(product.getProjectId())
                    .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId());

            if(res.status() == 504)
                continue;
            Assert.assertEquals("Статус ответа не равен ожидаемому", 200, res.status());
            orderStatus = res.jsonPath().get("status");

            System.out.println("orderStatus = " + orderStatus);
            counter = counter - 1;
        }
        log.info("Ордер статус итоговый " + orderStatus);
        if (!orderStatus.equals(exp_status.toLowerCase())) {
            String error = "";
            try {
                error = stateServiceSteps.GetErrorFromOrch(product);
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("Ошибка в GetErrorFromOrch " + e);
            }
            Assert.fail(String.format("Ошибка заказа продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product.toString(), orderStatus, error));
        }
    }


    @Step("Выполнение action \"{action}\"")
    public String executeAction(String action, IProduct product, JSONObject jsonData) {
        CostSteps costSteps = new CostSteps();
        Map<String,String> map = getItemIdByOrderId(action, product);
        log.info("Отправка запроса на выполнение действия '" + action + "' для продукта "+product.toString());

        //TODO: Возможно стоит сделать более детальную проверку на значение
        double cost = costSteps.getCostAction(map.get("name"), map.get("item_id"), product, jsonData);
        Assert.assertTrue("Стоимость после action отрицательная", cost >= 0);

//TODO: обработать кейс если экшен не найден
        return jsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", map.get("item_id"))
                .set("$.order.data", jsonData)
                .send(URL)
                .setProjectId(product.getProjectId())
                .patch("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/" + map.get("name"))
                .assertStatus(200)
                .jsonPath()
                .get("action_id");
    }

    @Step("Ожидание успешного выполнения action")
    public void checkActionStatus(String exp_status, IProduct product, String action_id) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String actionStatus = "";
        int counter = 20;
        log.info("Проверка статуса выполнения действия");
        while ((actionStatus.equals("pending") || actionStatus.equals("")) && counter > 0) {
            Waiting.sleep(30000);
            try {

//                action_status = new Http(URL)
//                        .setProjectId(product.getProjectId())
//                        .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/history/" + action_id)
//                        .assertStatus(200)
//                        .jsonPath().get("status");

                Http.HttpResponse res = new Http(URL)
                        .setProjectId(product.getProjectId())
                        .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/history/" + action_id);

                if(res.status() == 504)
                    continue;
                Assert.assertEquals("Статус ответа не равен ожидаемому", 200, res.status());
                actionStatus = res.jsonPath().get("status");



            } catch (JsonPathException e) {
                log.error("Error get status " + e.getMessage());
            }

            counter = counter - 1;
        }
        if (!actionStatus.equals(exp_status.toLowerCase())) {
            String error = "";
            try {
                error = stateServiceSteps.GetErrorFromOrch(product);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            Assert.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product.toString(), actionStatus, error));
        }
    }

    public String getDomainBySegment(IProduct product, String netSegment) {
        log.info("Получение домена для сегмента сети " + netSegment);
        return new Http(URL)
                .setProjectId(product.getProjectId())
                .get(String.format("order-service/api/v1/domains?net_segment_code=%s&include=total_count&page=1&per_page=25", netSegment))
                .assertStatus(200)
                .jsonPath()
                .get("list[0].code");
    }

    public String getProductId(IProduct product){
        log.info("Получение id для продукта " + product.getProductName());
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
                    .setProjectId(product.getProjectId())
                    .get(String.format("product-catalog/products/?is_open=true&env=%s&information_systems=%s&page=%s&per_page=100", product.getEnv().toLowerCase(), informationSystem.id, i))
                    .assertStatus(200)
                    .jsonPath()
                    .get(String.format("list.find{it.title == '%s' || it.title == '%s' || it.title == '%s'}.id", product.getProductName().toLowerCase(), product.getProductName().toUpperCase(), product.getProductName()));
            if(product_id != null)
                log.info("Id продукта = " + product_id);
            break;
        }
        return product_id;
    }

    public Map<String,String> getFlavorByProduct(IProduct product) {
        log.info("Получение флейвора для продукта " + product.getProductName());
        JsonPath jsonPath  = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("references/api/v1/pages/?directory__name=flavors&tags=" + product.getProductId())
                .assertStatus(200)
                .jsonPath();
        /*JSONArray jsonArray  = new Http(URL)
                .get("references/api/v1/pages/?directory__name=flavors&tags=" + product_id)
                .assertStatus(200)
                .toJsonArray();

        int i = jsonArray.get()*/

        String flavor = String.format("{\"flavor\":{\"cpus\":%s,\"name\":\"%s\",\"uuid\":\"%s\",\"memory\":%s}}", jsonPath.get("[1].data.cpus"), jsonPath.get("[1].name"), jsonPath.get("[1].id"), jsonPath.get("[1].data.memory"));
        Map<String,String> map = new HashMap<>();
        map.put("cpus", Integer.toString(jsonPath.get("[1].data.cpus")));
        map.put("memory", Integer.toString(jsonPath.get("[1].data.memory")));
        map.put("flavor", flavor);
        return map;
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
        Assert.assertNotNull("Action '" + action + "' не найден у продукта " + product.getProductName(), map.get("item_id"));
        return map;
    }

    @Step("Получение списка ресурсных пулов для категории {category} и среды {env}")
    public void getResourcesPool(String category, String env) {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
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

    @Step("Удаление всех заказов")
    public void deleteOrders(String env) {
        String action_title = "";
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        List orders = new Http(URL)
                .setProjectId(project.id)
                .get(String.format("order-service/api/v1/projects/%s/orders?include=total_count&page=1&per_page=100&f[category]=vm", project.id))
                .assertStatus(200)
                .jsonPath()
                .get("list.findAll{it.status == 'success'}.id");

        System.out.println("list = " + orders);

        for (int i = 0; i < orders.size(); i++) {
            try {
                String order_id = (String) orders.get(i);
                System.out.println("order_id = " + order_id);
                String productName = new Http(URL)
                        .setProjectId(project.id)
                        .get(String.format("order-service/api/v1/projects/%s/orders/%s", project.id, order_id))
                        .assertStatus(200)
                        .jsonPath()
                        .get("attrs.product_title");
                log.info("productName = " + productName);
                switch (productName) {
                    case ("Apache Kafka Cluster"):
                        action_title = "Удалить рекурсивно";
                        break;
                    default:
                        action_title = "Удалить";
                }


                log.info("Получение item_id для " + action_title);
                JsonPath jsonPath = new Http(URL)
                        .setProjectId(project.id)
                        .get("order-service/api/v1/projects/" + project.id + "/orders/" + order_id)
                        .jsonPath();
                String item_id = jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.item_id", action_title));
                String action = jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.actions.find{it.title.contains('%s')}.name", action_title, action_title));
                log.info("item_id = " + item_id);
                log.info("action = " + action);

                JsonPath response = jsonHelper.getJsonTemplate("/actions/template.json")
                        .set("$.item_id", item_id)
                        .send(URL)
                        .setProjectId(project.id)
                        .patch(String.format("order-service/api/v1/projects/%s/orders/%s/actions/%s", project.id, order_id, action))
                        .assertStatus(200)
                        .jsonPath();
            } catch (Throwable e){
                e.printStackTrace();
            }
        }


    }
}
