package steps.orderService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Windows;
import models.subModels.Item;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;
import steps.calculator.CalcCostSteps;
import steps.stateService.StateServiceSteps;
import steps.tarifficator.CostSteps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static core.helper.Configure.OrderServiceURL;

@Log4j2
public class OrderServiceSteps extends Steps {

    public static void checkOrderStatus(String exp_status, IProduct product) {
        String orderStatus = "";
        int counter = 60;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(30000);
            Response res = new Http(OrderServiceURL)
                    .setProjectId(product.getProjectId())
                    .get("/v1/projects/{}/orders/{}", product.getProjectId(), product.getOrderId())
                    .assertStatus(200);
            orderStatus = res.jsonPath().get("status");
            log.info("orderId={} orderStatus={}", product.getOrderId(), orderStatus);
            counter = counter - 1;
        }
        log.info("Итоговый статус заказа " + orderStatus);
        if (!orderStatus.equals(exp_status.toLowerCase())) {
            String error = "null";
            try {
                error = StateServiceSteps.GetErrorFromStateService(product.getOrderId());
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("Ошибка в GetErrorFromStateService " + e);
            }
            if (error.equals("null"))
                error = "Продукт не развернулся по таймауту";
            Assertions.fail(String.format("Ошибка заказа продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product, orderStatus, error));
        }
    }

    /**
     * Метод получает ID всех продуктов со статусом/статусами
     *
     * @param projectId ID проекта
     * @param statuses  статусы по которым нуно получить продукты
     *                  Возможные статусы: deprovisioned, damaged, pending ,changing, success
     * @return возвращает список ID продуктов
     */
    @Step("Получение продуктов со статусом")
    public static List<String> getProductsWithStatus(String projectId, String... statuses) {
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
            String endPoint = String.format("/v1/projects/%s/orders?include=total_count&page=" +
                            i + "&per_page=20&f" +
                            statusParams,
                    Objects.requireNonNull(projectId));
            //удалить &f если параметры statuses пустые, так как эндпоинт с &f не работает
            if (statuses.length == 0) {
                endPoint = endPoint.substring(0, endPoint.length() - 2);
            }
            idOfAllSuccessProductsOnOnePage = new Http(OrderServiceURL)
                    .setProjectId(projectId)
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

    @Step("Отправка action {action}")
    public static Response sendAction(String action, IProduct product, JSONObject jsonData, String projectId) {
        Item item = getItemIdByOrderIdAndActionTitle(action, product);
        return JsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", item.getId())
                .set("$.order.attrs", jsonData)
                .send(OrderServiceURL)
                .setProjectId(projectId)
                .patch("/v1/projects/{}/orders/{}/actions/{}", product.getProjectId(), product.getOrderId(), item.getName());
    }

    //{"order":{"attrs":{"client_types":"own","name":"dfghjkl","owner_cert":"dfghjkl"},"graph_version":"1.0.7"},"item_id":"ad08595a-c325-5434-9e5b-3d8b1bda7306"}
    @Step("Отправка action {action}")
    public static Response sendAction(String action, IProduct product, JSONObject jsonData) {
        Item item = getItemIdByOrderIdAndActionTitle(action, product);
        return JsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", item.getId())
                .set("$.order.attrs", jsonData)
                .send(OrderServiceURL)
                .patch("/v1/projects/{}/orders/{}/actions/{}", product.getProjectId(), product.getOrderId(), item.getName());
    }

    public static Response changeProjectForOrderRequest(IProduct product, Project target) {
        return new Http(OrderServiceURL)
                .body(new JSONObject(String.format("{target_project_name: \"%s\"}", target.getId())))
                .patch("/v1/projects/{}/orders/{}/change_project", product.getProjectId(), product.getOrderId());
    }

    @Step("Перенос продукта {product} в проект {target}")
    public static void changeProjectForOrder(IProduct product, Project target) {
        changeProjectForOrderRequest(product, target)
                .assertStatus(200);
        product.setProjectId(target.getId());
    }

    public static void executeAction(String action, IProduct product, JSONObject jsonData, String projectId) {
        executeAction(action, product, jsonData, null, projectId);
    }

    /**
     * Метод выполняет экшен по его имени
     *
     * @param projectId id проекта
     * @param action    имя экшена
     * @param product   объект продукта
     * @param jsonData  параметр дата в запросе, к примеру: "order":{"data":{}}}
     */
    @Step("Выполнение action \"{action}\"")
    public static void executeAction(String action, IProduct product, JSONObject jsonData, ProductStatus status, String projectId) {
        //Получение item'ов для экшена
        Item item = getItemIdByOrderIdAndActionTitle(action, product);
        log.info("Отправка запроса на выполнение действия '{}' продукта {}", action, product);
        //TODO: Возможно стоит сделать более детальную проверку на значение

        AtomicReference<Float> costPreBilling = new AtomicReference<>();
        AtomicReference<String> actionId = new AtomicReference<>();

        Assertions.assertAll("Проверка выполнения action - " + item.getName() + " у продукта " + product.getOrderId(),
                () -> {
                    costPreBilling.set(CostSteps.getCostAction(item.getName(), item.getId(), product, jsonData));
                    Assertions.assertTrue(costPreBilling.get() >= 0, "Стоимость после action отрицательная");
                },
                () -> {
                    actionId.set(sendAction(action, product, jsonData, projectId)
                            .assertStatus(200)
                            .jsonPath()
                            .get("action_id"));

                    checkActionStatusMethod("success", product, actionId.get());
                    if (Objects.nonNull(status))
                        product.setStatus(status);
                });

        if (costPreBilling.get() != null) {
            Float cost = null;
            for (int i = 0; i < 20; i++) {
                Waiting.sleep(20000);
                cost = CalcCostSteps.getCostByUid(product);
                if (cost == null)
                    continue;
                if (Math.abs(cost - costPreBilling.get()) > 0.00001)
                    continue;
                break;
            }
            Assertions.assertNotNull(cost, "Стоимость списания равна null");
            Assertions.assertEquals(costPreBilling.get(), cost, 0.00001, "Стоимость предбиллинга экшена отличается от стоимости списаний после action - " + action);
        }
    }

    /**
     * Метод выполняет экшен по его имени
     *
     * @param action   имя экшена
     * @param product  объект продукта
     * @param jsonData параметр дата в запросе, к примеру: "order":{"data":{}}}
     */
    @Step("Выполнение action \"{action}\"")
    public static void executeAction(String action, IProduct product, JSONObject jsonData) {
        //Получение item'ов для экшена
        Item item = getItemIdByOrderIdAndActionTitle(action, product);
        log.info("Отправка запроса на выполнение действия '{}' продукта {}", action, product);
        //TODO: Возможно стоит сделать более детальную проверку на значение

        AtomicReference<Float> costPreBilling = new AtomicReference<>();
        AtomicReference<String> actionId = new AtomicReference<>();

        Assertions.assertAll("Проверка выполнения action - " + item.getName() + " у продукта " + product.getOrderId(),
                () -> {
                    costPreBilling.set(CostSteps.getCostAction(item.getName(), item.getId(), product, jsonData));
                    Assertions.assertTrue(costPreBilling.get() >= 0, "Стоимость после action отрицательная");
                },
                () -> {
                    actionId.set(sendAction(action, product, jsonData)
                            .assertStatus(200)
                            .jsonPath()
                            .get("action_id"));
                    checkActionStatusMethod("success", product, actionId.get());
                    if (costPreBilling.get() != null) {
                        Float cost = null;
                        for (int i = 0; i < 20; i++) {
                            Waiting.sleep(20000);
                            cost = CalcCostSteps.getCostByUid(product);
                            if (cost == null)
                                continue;
                            if (Math.abs(cost - costPreBilling.get()) > 0.00001)
                                continue;
                            break;
                        }
                        Assertions.assertNotNull(cost, "Стоимость списания равна null");
                        Assertions.assertEquals(costPreBilling.get(), cost, 0.00001, "Стоимость предбиллинга экшена отличается от стоимости списаний после action - " + action);
                    }
                });

    }

    @Step("Ожидание успешного выполнения action")
    public static void checkActionStatusMethod(String exp_status, IProduct product, String action_id) {
        String actionStatus = "";
        int counter = 22;
        log.info("Проверка статуса выполнения действия");
        while ((actionStatus.equals("pending") || actionStatus.equals("changing") || actionStatus.equals("")) && counter > 0) {
            Waiting.sleep(30000);
            actionStatus = new Http(OrderServiceURL)
                    .setProjectId(product.getProjectId())
                    .get("/v1/projects/{}/orders/{}/actions/history/{}", product.getProjectId(), product.getOrderId(), action_id)
                    .assertStatus(200)
                    .jsonPath()
                    .get("status");
            counter = counter - 1;
        }
        if (!actionStatus.equals(exp_status.toLowerCase())) {
            String error = null;
            try {
                error = StateServiceSteps.GetErrorFromStateService(product.getOrderId());
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (Objects.isNull(error))
                error = "Действие не выполнено по таймауту";
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product, actionStatus, error));
        }
    }

    @Step("Получение домена для сегмента сети {netSegment}")
    public static String getDomainBySegment(IProduct product, String netSegment) {
        return new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("/v1/domains?net_segment_code={}&page=1&per_page=25", netSegment)
                .assertStatus(200)
                .jsonPath()
                .get("list.collect{e -> e}.shuffled()[0].code");
    }

    @Step("Получение домена для проекта {project}")
    public static String getDomainByProject(String project) {
        if (Configure.ENV.equals("ift")) {
            return new Http(OrderServiceURL)
                    .get("/v1/domains?project_name={}&with_deleted=false&page=1&per_page=25", project)
                    .assertStatus(200)
                    .jsonPath()
                    .get("list.find{it.code=='corp.dev.vtb'}.code");
        } else {
            return new Http(OrderServiceURL)
                    .get("/v1/domains?project_name={}&with_deleted=false&page=1&per_page=25", project)
                    .assertStatus(200)
                    .jsonPath()
                    .get("list.collect{e -> e}.shuffled()[0].code");
        }
    }

    public static String getDataCentreBySegment(IProduct product, String netSegment) {
        log.info("Получение ДЦ для сегмента сети " + netSegment);
        return new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("/v1/data_centers?net_segment_code={}&page=1&per_page=25", netSegment)
                .assertStatus(200)
                .jsonPath()
                .get("list.collect{e -> e}.shuffled()[0].code");
    }

    /**
     * @param action  экшен
     * @param product продукт
     * @return - возвращаем ID айтема
     */
    public static Item getItemIdByOrderIdAndActionTitle(String action, IProduct product) {
        log.info("Получение item_id для " + Objects.requireNonNull(action));
        //Отправка запроса на получение айтема
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(Objects.requireNonNull(product).getProjectId())
                .get("/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .assertStatus(200)
                .jsonPath();

        Item item = new Item();
        //Получаем все item ID по name, например: "expand_mount_point"
        item.setId(jsonPath.get(String.format("data.find{it.actions.find{it.name=='%s'}}.item_id", action)));
        //Получаем все item name
        item.setName(jsonPath.get(String.format("data.find{it.actions.find{it.name=='%s'}}.actions.find{it.name=='%s'}.name", action, action)));
        //Достаем item ID и item name и сохраняем в объект Item
        if (item.getId() == null) {
            item.setId(jsonPath.get(String.format("data.find{it.actions.find{it.name.contains('%s')}}.item_id", action)));
            item.setName(jsonPath.get(String.format("data.find{it.actions.find{it.name.contains('%s')}}.actions.find{it.name.contains('%s')}.name", action, action)));
        }

        Assertions.assertNotNull(item.getId(), "Action '" + action + "' не найден у продукта " + product.getProductName());
        return item;
    }

    /**
     * Метод проверяет состояние продукта: включен или выключен
     *
     * @param product продукт
     * @return true если включен, false выключен
     */
    public static boolean productStatusIs(IProduct product, ProductStatus status) {
        log.info("Получение статуса для для продукта " + Objects.requireNonNull(product));
        //Отправка запроса на получение айтема
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(Objects.requireNonNull(product).getProjectId())
                .get("/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .assertStatus(200)
                .jsonPath();
        if (status.equals(ProductStatus.STARTED)) {
            log.info("Статус продукта ВКЛЮЧЕН");
            return jsonPath.getString("power_status.status").equals("on");
        } else {
            log.info("Статус продукта ВЫКЛЮЧЕН");
            return jsonPath.getString("power_status.status").equals("off");
        }
    }

    @Step("Получение списка ресурсных пулов для категории {category} и проекта {projectId}")
    public static List<ResourcePool> getResourcesPoolList(String category, String projectId) {
        String jsonArray = new Http(OrderServiceURL)
                .setProjectId(projectId)
                .get("/v1/products/resource_pools?category={}&project_name={}&resource_type=cluster:openshift", category, projectId)
                .assertStatus(200)
                .toJson()
                .getJSONArray("list")
                .toString();
        Type type = new TypeToken<List<ResourcePool>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    public static <T extends Comparable<T>> Comparable<T> getProductsField(IProduct product, String path) {
        return (Comparable<T>) getProductsField(product, path, Comparable.class);
    }


    @Step("Получение значения по пути {path}")
    public static Object getProductsField(IProduct product, String path, Class<?> clazz) {
        Object s;
        log.info("getFiledProduct path: " + path);
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("/v1/projects/{}/orders/{}", Objects.requireNonNull(product).getProjectId(), product.getOrderId())
                .assertStatus(200)
                .jsonPath();
        s = jsonPath.get(path);
        log.info(String.format("getFiledProduct return: %s", s));
        Assertions.assertNotNull(s, "По path '" + path + "' не найден объект в response " + jsonPath.prettify());
        return s;
    }

    @Step("Удаление всех заказов")
    public static void deleteOrders(String env) {
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(Objects.requireNonNull(env)))
                .isForOrders(true).build().createObject();
        List<String> orders = new Http(OrderServiceURL)
                .setProjectId(project.id)
                .get("/v1/projects/{}/orders?include=total_count&page=1&per_page=100&f[status][]=success", project.id)
                .assertStatus(200)
                .jsonPath()
                .get("list.findAll{it.status == 'success'}.id");
        log.trace("list = " + orders);
        for (String order : orders) {
            try {
                JsonPath jsonPath = new Http(OrderServiceURL)
                        .setProjectId(project.id)
                        .get("/v1/projects/" + project.id + "/orders/" + order)
                        .jsonPath();
                String itemId = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.item_id");
                String action = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.actions.find{it.type == 'delete'}.name");
                log.trace("item_id = " + itemId);
                log.trace("action = " + action);

                JsonHelper.getJsonTemplate("/actions/template.json")
                        .set("$.item_id", itemId)
                        .send(OrderServiceURL)
                        .setProjectId(project.id)
                        .patch("/v1/projects/{}/orders/{}/actions/{}", project.id, order, action)
                        .assertStatus(200);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteProduct(IProduct product) {
        new Http(OrderServiceURL)
                .delete("/v1/projects/{}/orders/{}", product.getProjectId(), product.getOrderId());
    }
}
