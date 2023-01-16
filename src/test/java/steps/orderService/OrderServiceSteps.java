package steps.orderService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.orderService.ResourcePool;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.interfaces.ProductStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;
import steps.calculator.CalcCostSteps;
import steps.stateService.StateServiceSteps;
import steps.tarifficator.CostSteps;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static core.helper.Configure.OrderServiceURL;

@Log4j2
public class OrderServiceSteps extends Steps {

    public static void checkOrderStatus(String exp_status, IProduct product) {
        String orderStatus = "";
        int counter = 80;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(20000);
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
                error = StateServiceSteps.getErrorFromStateService(product.getOrderId());
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("Ошибка в GetErrorFromStateService " + e);
            }
            if (Objects.isNull(error))
                error = "Отсутствует информация о заказе в state service";
            if (error.equals("null"))
                error = "Продукт не развернулся по таймауту";
            Assertions.fail(String.format("Ошибка заказа продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product, orderStatus, error));
        }
    }

    public static String getStatus(IProduct product) {
        return new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("/v1/projects/{}/orders/{}", product.getProjectId(), product.getOrderId())
                .assertStatus(200)
                .jsonPath()
                .getString("status");
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

    @Step("Получение продуктов со всеми статусами")
    public static List<String> getProductsWithAllStatus(String projectId) {
        List<String> idOfAllSuccessProductsOnOnePage;
        List<String> idOfAllSuccessProducts = new ArrayList<>();
        int i = 0;
        do {
            i++;
            String endPoint = String.format("/v1/projects/%s/orders?include=total_count&page=" +
                            i + "&per_page=20",
                    Objects.requireNonNull(projectId));
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
    public static Response sendAction(String action, IProduct product, JSONObject jsonData, String projectId, String filter) {
        String item = getItemIdByOrderIdAndActionTitle(action, product, filter);
        return JsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", item)
                .set("$.order.attrs", jsonData)
                .send(OrderServiceURL)
                .setProjectId(projectId)
                .patch("/v1/projects/{}/orders/{}/actions/{}", product.getProjectId(), product.getOrderId(), action);
    }

    //{"order":{"attrs":{"client_types":"own","name":"dfghjkl","owner_cert":"dfghjkl"},"graph_version":"1.0.7"},"item_id":"ad08595a-c325-5434-9e5b-3d8b1bda7306"}
    @Step("Отправка action {action}")
    public static Response sendAction(String action, IProduct product, JSONObject jsonData, String filter) {
        String item = getItemIdByOrderIdAndActionTitle(action, product, filter);
        return JsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", item)
                .set("$.order.attrs", jsonData)
                .send(OrderServiceURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .patch("/v1/projects/{}/orders/{}/actions/{}", product.getProjectId(), product.getOrderId(), action);
    }

    @Step("Добавление действия {actionName} заказа и регистрация его в авторайзере")
    public static void registrationAction(String actionName) {
        new Http(OrderServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("action_name", actionName))
                .post("/v1/orders/actions");
    }

    public static Response changeProjectForOrderRequest(IProduct product, Project target) {
        return new Http(OrderServiceURL)
                .setRole(Role.CLOUD_ADMIN)
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
        executeAction(action, product, jsonData, null, projectId, "");
    }

    public static void executeActionWidthFilter(String action, IProduct product, JSONObject jsonData, String projectId, String filter) {
        executeAction(action, product, jsonData, null, projectId, filter);
    }

    public static void executeAction(String action, IProduct product, JSONObject jsonData, ProductStatus status, String projectId) {
        executeAction(action, product, jsonData, status, projectId, "");
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
    public static void executeAction(String action, IProduct product, JSONObject jsonData, ProductStatus status, String projectId, String filter) {
        //Получение item'ов для экшена
        waitStatus(Duration.ofMinutes(10), product);
        String item = getItemIdByOrderIdAndActionTitle(action, product, filter);
        log.info("Отправка запроса на выполнение действия '{}' продукта {}", action, product);
        //TODO: Возможно стоит сделать более детальную проверку на значение

        AtomicReference<Float> costPreBilling = new AtomicReference<>();
        AtomicReference<String> actionId = new AtomicReference<>();

        Assertions.assertAll("Проверка выполнения action - " + action + " у продукта " + product.getOrderId(),
                () -> {
                    costPreBilling.set(CostSteps.getCostAction(action, item, product, jsonData));
                    Assertions.assertTrue(costPreBilling.get() >= 0, "Стоимость после action отрицательная");
                },
                () -> {
                    actionId.set(sendAction(action, product, jsonData, projectId, filter)
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
        String item = getItemIdByOrderIdAndActionTitle(action, product, "");
        log.info("Отправка запроса на выполнение действия '{}' продукта {}", action, product);
        //TODO: Возможно стоит сделать более детальную проверку на значение

        AtomicReference<Float> costPreBilling = new AtomicReference<>();
        AtomicReference<String> actionId = new AtomicReference<>();

        Assertions.assertAll("Проверка выполнения action - " + action + " у продукта " + product.getOrderId(),
                () -> {
                    costPreBilling.set(CostSteps.getCostAction(action, item, product, jsonData));
                    Assertions.assertTrue(costPreBilling.get() >= 0, "Стоимость после action отрицательная");
                },
                () -> {
                    actionId.set(sendAction(action, product, jsonData, "")
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
        int counter = 45;
        log.info("Проверка статуса выполнения действия");
        while ((actionStatus.equals("pending") || actionStatus.equals("changing") || actionStatus.equals("")) && counter > 0) {
            Waiting.sleep(20000);
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
                error = StateServiceSteps.getErrorFromStateService(product.getOrderId());
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (Objects.isNull(error))
                error = "Действие не выполнено по таймауту";
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product, actionStatus, error));
        }
    }

    private static void waitStatus(Duration timeout, IProduct product) {
        Instant startTime = Instant.now();
        String status;
        do {
            Waiting.sleep(20000);
            status = getStatus(product);
        } while (status.equals("pending") || status.equals("changing")
                && Duration.between(startTime, Instant.now()).compareTo(timeout) < 0);
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
        Organization organization = Organization.builder().build().createObject();
        if (Configure.ENV.equals("ift")) {
            return new Http(OrderServiceURL)
                    .setRole(Role.ORDER_SERVICE_ADMIN)
                    .get("/v1/domains?project_name={}&with_deleted=false&page=1&per_page=25&organzation={}", project, organization.getName())
                    .assertStatus(200)
                    .jsonPath()
                    .get("list.find{it.code=='corp.dev.vtb'}.code");
        } else {
            return new Http(OrderServiceURL)
                    .setRole(Role.ORDER_SERVICE_ADMIN)
                    .get("/v1/domains?project_name={}&with_deleted=false&page=1&per_page=25&organzation={}", project, organization.getName())
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
    public static String getItemIdByOrderIdAndActionTitle(String action, IProduct product, String filter) {
        String id;
        log.info("Получение item_id для " + Objects.requireNonNull(action));
        //Отправка запроса на получение айтема
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(Objects.requireNonNull(product).getProjectId())
                .get("/v1/projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
                .assertStatus(200)
                .jsonPath();

        if (!filter.equals(""))
            filter = "it.data.config." + filter + " && ";
        id = jsonPath.getString(String.format("data.find{%sit.actions.find{it.name=='%s'}}.item_id", filter, action));

        List<Object> pathList = jsonPath.getList(String.format("data.find{%sit.actions.find{it.name!=''}}.actions.title", filter));
        String actions = "-";
        if(Objects.nonNull(pathList))
            actions = Arrays.toString(pathList.toArray());
        Assertions.assertNotEquals("", id, "Action '" + action + "' не найден у продукта " + product.getProductName() + "\n Найденные экшены: " + actions);

        return id;
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
            return jsonPath.getString("power_status.find{it.containsKey('status')}.status").equals("on");
        } else {
            log.info("Статус продукта ВЫКЛЮЧЕН");
            return jsonPath.getString("power_status.find{it.containsKey('status')}.status").equals("off");
        }
    }

    @Step("Получение списка ресурсных пулов для категории {category} и проекта {projectId}")
    public static List<ResourcePool> getResourcesPoolList(String category, String projectId, String productName) {
        String jsonArray = new Http(OrderServiceURL)
                .setProjectId(projectId)
                .get("/v1/products/resource_pools?category={}&project_name={}&resource_type=cluster:openshift&quota[cpu]=1&quota[memory]=1&product_name={}",
                        category, projectId, productName)
                .assertStatus(200)
                .toJson()
                .getJSONArray("list")
                .toString();
        Type type = new TypeToken<List<ResourcePool>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    public static <T extends Comparable<T>> Comparable<T> getProductsField(IProduct product, String path) {
        return (Comparable<T>) getProductsField(product, path, null);
    }

    public static Object getProductsField(IProduct product, String path, Class<?> clazz) {
        return getProductsField(product,path,clazz, true);
    }

    @Step("Получение значения по пути {path}")
    public static Object getProductsField(IProduct product, String path, Class<?> clazz, boolean assertion) {
        Object s;
        log.info("getFiledProduct path: " + path);
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("/v1/projects/{}/orders/{}", Objects.requireNonNull(product).getProjectId(), product.getOrderId())
                .assertStatus(200)
                .jsonPath();
        if (Objects.nonNull(clazz))
            s = jsonPath.getObject(path, clazz);
        else
            s = jsonPath.get(path);
        if(assertion) {
            log.info(String.format("getFiledProduct return: %s", s));
            Assertions.assertNotNull(s, "По path '" + path + "' не найден объект в response " + jsonPath.prettify());
        }
        return s;
    }

    @Step("Получение объекта класса по пути {path}")
    public static Object getObjectClass(IProduct product, String path, Class<?> clazz) {
        String object = new Gson().toJson(getProductsField(product, path, Map.class, false));
        return JsonHelper.deserialize(object, clazz);
    }

    @Step("Получение сетевого сегмента для продукта {product}")
    public static String getNetSegment(IProduct product) {
        return Objects.requireNonNull(new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("/v1/net_segments?project_name={}&with_restrictions=true&product_name={}&page=1&per_page=25",
                        Objects.requireNonNull(product).getProjectId(), product.getProductCatalogName())
                .assertStatus(200)
                .jsonPath()
                .getString("list[0].code"), "Список сетевых сегментов пуст");
    }

    @Step("Удаление всех заказов")
    public static void deleteOrders(String env) {
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(Objects.requireNonNull(env)))
                .isForOrders(true).build().createObject();
        List<String> orders = new Http(OrderServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}/orders?include=total_count&page=1&per_page=100&f[status][]=success", project.id)
                .assertStatus(200)
                .jsonPath()
                .get("list.findAll{it.status == 'success'}.id");
        log.trace("list = " + orders);
        for (String order : orders) {
            try {
                JsonPath jsonPath = new Http(OrderServiceURL)
//                        .setProjectId(project.id)
                        .setRole(Role.CLOUD_ADMIN)
                        .get("/v1/projects/" + project.id + "/orders/" + order)
                        .jsonPath();
                String itemId = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.item_id");
                String action = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.actions.find{it.type == 'delete'}.name");
                log.trace("item_id = " + itemId);
                log.trace("action = " + action);

                JsonHelper.getJsonTemplate("/actions/template.json")
                        .set("$.item_id", itemId)
                        .send(OrderServiceURL)
                        .setRole(Role.CLOUD_ADMIN)
                        .patch("/v1/projects/{}/orders/{}/actions/{}", project.id, order, action)
                        .assertStatus(200);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (Configure.ENV.equalsIgnoreCase("IFT")) {
            orders = new Http(OrderServiceURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/v1/projects/{}/orders?include=total_count&page=1&per_page=100&f[status][]=success&f[status][]=changing&f[status][]=damaged&f[status][]=failure&f[status][]=pending", project.id)
                    .assertStatus(200)
                    .jsonPath()
                    .get("list.findAll{!(it.status == 'success' && it.deletable == true)}.id");

            StringJoiner params = new StringJoiner("&order_ids[]=", "order_ids[]=", "");
            orders.forEach(params::add);

            new Http(OrderServiceURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .delete("/v1/orders?&force=false&{}", params.toString());
            log.trace("list = " + orders);
        }

    }

    public static void deleteProduct(IProduct product) {
        new Http(OrderServiceURL)
                .delete("/v1/projects/{}/orders/{}", product.getProjectId(), product.getOrderId());
    }
}
