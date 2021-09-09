package steps.orderService;

import core.exception.DeferredException;
import core.helper.Configure;
import core.helper.Http;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.InformationSystem;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import steps.Steps;
import steps.calculator.CalcCostSteps;
import steps.stateService.StateServiceSteps;
import steps.tarifficator.CostSteps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class OrderServiceSteps extends Steps {
    public static final String URL = Configure.getAppProp("host_kong");

    public void checkOrderStatus(String exp_status, IProduct product) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String orderStatus = "";
        int counter = 60;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(30000);


//            orderStatus = new Http(URL)
//                    .setProjectId(product.getProjectId())
//                    .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
//                    .assertStatus(200)
//                    .jsonPath()
//                    .get("status");

            Http res = new Http(URL)
                    .setProjectId(product.getProjectId())
                    .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId());

//            if(res.status() == 504)
//                continue;
            Assert.assertEquals("Статус ответа не равен ожидаемому", 200, res.status());
            orderStatus = res.jsonPath().get("status");

            log.info("orderStatus = " + orderStatus);
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

    //deprovisioned, damaged, pending ,changing, success
    @Step("Получение продуктов со статусом success")
    public List<String> getProductsWithStatus(String env, String... statuses) {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(true)
                .getEntity();
        List<String> idOfAllSuccessProductsOnOnePage;
        List<String> idOfAllSuccessProducts = new ArrayList<>();
        int i = 0;
        StringBuilder statusParams = new StringBuilder();
        for (String status : statuses) {
            statusParams.append("[status][]=").append(status).append("&f");
        }
        //удалить &f если параметры statuses не пустые, так как эндпоинт с &f не работает
        if (statuses.length > 0) {
            statusParams.delete(statusParams.length() - 2, statusParams.length());
        }
        do {
            i++;
            String endPoint = String.format("order-service/api/v1/projects/%s/orders?include=total_count&page=" +
                            i + "&per_page=20&f" +
                            statusParams,
                    project.id);
            //удалить &f если параметры statuses пустые, так как эндпоинт с &f не работает
            if (statuses.length == 0) {
                endPoint = endPoint.substring(0, endPoint.length() - 2);
            }
            idOfAllSuccessProductsOnOnePage = new Http(URL)
                    .setProjectId(project.id)
                    .get(endPoint)
                    .assertStatus(200)
                    .jsonPath()
                    .getList("list.id");
            idOfAllSuccessProducts.addAll(idOfAllSuccessProductsOnOnePage);
        } while (idOfAllSuccessProductsOnOnePage.size() != 0);
        log.info("Список ID проектов со статусом success " + idOfAllSuccessProducts);
        log.info("Кол-во продуктов " + idOfAllSuccessProducts.size());
        return idOfAllSuccessProducts;
    }

    @Step("Выполнение action \"{action}\"")
    public void executeAction(String action, IProduct product, JSONObject jsonData) {
        CostSteps costSteps = new CostSteps();
        CalcCostSteps calcCostSteps = new CalcCostSteps();
        Map<String, String> map = getItemIdByOrderId(action, product);
        log.info("Отправка запроса на выполнение действия '" + action + "' для продукта " + product.toString());
        DeferredException exception = new DeferredException();
        //TODO: Возможно стоит сделать более детальную проверку на значение
        Float costPreBilling = null;
        try {
            costPreBilling = costSteps.getCostAction(map.get("name"), map.get("item_id"), product, jsonData);
            Assert.assertTrue("Стоимость после action отрицательная", costPreBilling >= 0);
        } catch (Throwable e) {
            exception.addException(e, product.getOrderId());
        }

        String actionId = null;
        try {
//TODO: обработать кейс если экшен не найден
            actionId = jsonHelper.getJsonTemplate("/actions/template.json")
                    .set("$.item_id", map.get("item_id"))
                    .set("$.order.data", jsonData)
                    .send(URL)
                    .setProjectId(product.getProjectId())
                    .patch("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/" + map.get("name"))
                    .assertStatus(200)
                    .jsonPath()
                    .get("action_id");
        } catch (Throwable e) {
            exception.addException(e, product.getOrderId());
        }
        try {
            checkActionStatusMethod("success", product, actionId);
        } catch (Throwable e) {
            exception.addException(e, product.getOrderId());
        }
        try {
            if (costPreBilling != null) {
                Float cost = null;
                for (int i = 0; i < 10; i++) {
                    Waiting.sleep(20000);
                    cost = calcCostSteps.getCostByUid(product);
                    if (cost == null)
                        continue;
                    if (Float.compare(cost, costPreBilling) > 0.00001)
                        continue;
                    break;
                }
                Assert.assertNotNull("Стоимость списания равна null", cost);
                Assert.assertEquals("Стоимость предбиллинга экшена отличается от стоимости списаний после action - " + action, costPreBilling, cost, 0.00001);
            }

        } catch (Throwable e) {
            exception.addException(e, product.getOrderId());
        }
        exception.trowExceptionIfNotEmpty();
    }

    @Step("Ожидание успешного выполнения action")
    public void checkActionStatusMethod(String exp_status, IProduct product, String action_id) {
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

                Http res = new Http(URL)
                        .setProjectId(product.getProjectId())
                        .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId() + "/actions/history/" + action_id);

//                if(res.status() == 504)
//                    continue;
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

    public String getProductId(IProduct product) {
        log.info("Получение id для продукта " + product.getProductName());
        InformationSystem informationSystem = cacheService.entity(InformationSystem.class)
                .forOrders(true)
                .getEntity();
        String product_id = "";
        ProjectEnvironment projectEnvironment = cacheService.entity(ProjectEnvironment.class)
                .forOrders(true)
                .withField("env", product.getEnv()).getEntity();
        int total_count = new Http(URL)
                .setProjectId(product.getProjectId())
                .get(String.format("product-catalog/products/?is_open=true&env=%s&information_systems=%s&page=1&per_page=100", projectEnvironment.envType.toLowerCase(), informationSystem.id))
                .assertStatus(200)
                .jsonPath()
                .get("meta.total_count");

        int countOfIteration = total_count / 100 + 1;
        for (int i = 1; i <= countOfIteration; i++) {
            product_id = new Http(URL)
                    .setProjectId(product.getProjectId())
                    .get(String.format("product-catalog/products/?is_open=true&env=%s&information_systems=%s&page=%s&per_page=100", projectEnvironment.envType.toLowerCase(), informationSystem.id, i))
                    .assertStatus(200)
                    .jsonPath()
                    .get(String.format("list.find{it.title == '%s' || it.title == '%s' || it.title == '%s'}.id", product.getProductName().toLowerCase(), product.getProductName().toUpperCase(), product.getProductName()));
            if (product_id != null) {
                log.info("Id продукта = " + product_id);
                break;
            }
        }
        Assertions.assertNotNull(product_id, String.format("ID продукта: %s, не найден", product.getProductName()));
        return product_id;
    }

    public Map<String, String> getItemIdByOrderId(String action, IProduct product) {
        log.info("Получение item_id для " + action);
        JsonPath jsonPath = new Http(URL)
                .setProjectId(product.getProjectId())
                .get("order-service/api/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .jsonPath();

        Map<String, String> map = new HashMap<>();

        map.put("item_id", jsonPath.get(String.format("data.find{it.actions.find{it.title=='%s'}}.item_id", action)));
        map.put("name", jsonPath.get(String.format("data.find{it.actions.find{it.title=='%s'}}.actions.find{it.title=='%s'}.name", action, action)));

        if (map.get("item_id") == null) {
            map.put("item_id", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.item_id", action)));
            map.put("name", jsonPath.get(String.format("data.find{it.actions.find{it.title.contains('%s')}}.actions.find{it.title.contains('%s')}.name", action, action)));
        }

        Assert.assertNotNull("Action '" + action + "' не найден у продукта " + product.getProductName(), map.get("item_id"));
        return map;
    }

    @Step("Получение списка ресурсных пулов для категории {category} и среды {env}")
    public void getResourcesPool(String category, String env) {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(true)
                .getEntity();
        JSONObject jsonObject = new Http(URL)
                .setProjectId(project.id)
                .get(String.format("order-service/api/v1/products/resource_pools?category=%s&project_name=%s", category, project.id))
                .assertStatus(200)
                .toJson();
        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
        for (Object object : jsonArray) {
            JSONObject j = (JSONObject) object;
            ResourcePool resourcePool = ResourcePool.builder()
                    .id(j.getString("id"))
                    .label(j.getString("label"))
                    .projectId(project.id)
                    .build();
            cacheService.saveEntity(resourcePool);
        }
    }

    public <T extends Comparable<T>> Comparable<T> getFiledProduct(@NotNull IProduct product, String path) {
        Comparable<T> s;
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
        String action_title;
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .forOrders(true)
                .getEntity();
        List<String> orders = new Http(URL)
                .setProjectId(project.id)
                .get(String.format("order-service/api/v1/projects/%s/orders?include=total_count&page=1&per_page=100&f[status][]=success", project.id))
                .assertStatus(200)
                .jsonPath()
                .get("list.findAll{it.status == 'success'}.id");

        log.info("list = " + orders);

        for (String order : orders) {
            try {
                System.out.println("order_id = " + order);
                String productName = new Http(URL)
                        .setProjectId(project.id)
                        .get(String.format("order-service/api/v1/projects/%s/orders/%s", project.id, order))
                        .assertStatus(200)
                        .jsonPath()
                        .get("attrs.product_title");
                log.info("productName = " + productName);
                if ("Apache Kafka Cluster".equals(productName)) {
                    action_title = "Удалить";
                } else {
                    action_title = "Удалить рекурсивно";
                }
                log.info("Получение item_id для " + action_title);
                JsonPath jsonPath = new Http(URL)
                        .setProjectId(project.id)
                        .get("order-service/api/v1/projects/" + project.id + "/orders/" + order)
                        .jsonPath();
                String item_id = jsonPath.get(String.format("data.find{it.actions.find{it.title.startsWith('%s')}}.item_id", action_title));
                String action = jsonPath.get(String.format("data.find{it.actions.find{it.title.startsWith('%s')}}.actions.find{it.title.contains('%s')}.name", action_title, action_title));
                log.info("item_id = " + item_id);
                log.info("action = " + action);

                JsonPath response = jsonHelper.getJsonTemplate("/actions/template.json")
                        .set("$.item_id", item_id)
                        .send(URL)
                        .setProjectId(project.id)
                        .patch(String.format("order-service/api/v1/projects/%s/orders/%s/actions/%s", project.id, order, action))
                        .assertStatus(200)
                        .jsonPath();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
