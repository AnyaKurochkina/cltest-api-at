package steps.orderService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import core.helper.http.Response;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
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
import java.util.function.Predicate;

import static core.enums.Role.CLOUD_ADMIN;
import static core.enums.Role.ORDER_SERVICE_ADMIN;
import static core.helper.Configure.orderServiceAdminURL;
import static core.helper.Configure.orderServiceURL;

@Log4j2
public class OrderServiceSteps extends Steps {

    public static void checkOrderStatus(String exp_status, IProduct product) {
        String orderStatus = "";
        int counter = 140;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.isEmpty() || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(20000);
            Response res = new Http(orderServiceURL)
                    .disableAttachmentLog()
                    .setProjectId(product.getProjectId(), ORDER_SERVICE_ADMIN)
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
                error = StateServiceSteps.getErrorFromStateService(product.getProjectId(), product.getOrderId());
            } catch (Throwable e) {
                log.error("Ошибка в GetErrorFromStateService " + e);
            }
            if (Objects.isNull(error))
                error = "Отсутствует информация о заказе в state service";
            if (error.equals("null"))
                error = "Продукт не развернулся по таймауту";
            Assertions.fail(String.format("Ошибка заказа продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product, orderStatus, error));
        }
    }

    public static String getStatusOrder(String orderId, String projectId) {
        return new Http(orderServiceURL)
                .disableAttachmentLog()
                .setProjectId(projectId, ORDER_SERVICE_ADMIN)
                .get("/v1/projects/{}/orders/{}", projectId, orderId)
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    public static String getStatusAction(String orderId, String actionId, String projectId) {
        return new Http(orderServiceURL)
                .setProjectId(projectId, ORDER_SERVICE_ADMIN)
                .get("/v1/projects/{}/orders/{}/actions/history/{}", projectId, orderId, actionId)
                .assertStatus(200)
                .jsonPath()
                .get("status");
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
            idOfAllSuccessProductsOnOnePage = new Http(orderServiceURL)
                    .setRole(ORDER_SERVICE_ADMIN)
                    .get(endPoint)
                    .assertStatus(200)
                    .jsonPath()
                    .getList("list.id");
            idOfAllSuccessProducts.addAll(idOfAllSuccessProductsOnOnePage);
        } while (!idOfAllSuccessProductsOnOnePage.isEmpty());
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
            idOfAllSuccessProductsOnOnePage = new Http(orderServiceURL)
                    .setRole(CLOUD_ADMIN)
                    .get(endPoint)
                    .assertStatus(200)
                    .jsonPath()
                    .getList("list.id");
            idOfAllSuccessProducts.addAll(idOfAllSuccessProductsOnOnePage);
        } while (!idOfAllSuccessProductsOnOnePage.isEmpty());
        log.info("Список ID проектов со статусом success " + idOfAllSuccessProducts);
        log.info("Кол-во продуктов " + idOfAllSuccessProducts.size());
        return idOfAllSuccessProducts;
    }

    @Step("Отправка action {action.name}")
    public static Response sendAction(ActionParameters action) {
        final Http request = JsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", action.getItemId())
                .set("$.order.attrs", action.getData())
                .send(orderServiceURL);
        if (Objects.isNull(action.getRole()))
            return request.setProjectId(action.getProjectId(), ORDER_SERVICE_ADMIN)
                    .patch("/v1/projects/{}/orders/{}/actions/{}", action.getProjectId(), action.getOrderId(), action.getName());
        return request.setRole(action.getRole())
                .patch("/v1/projects/{}/orders/{}/actions/{}", action.getProjectId(), action.getOrderId(), action.getName());
    }

    @Step("Добавление действия {actionName} заказа и регистрация его в авторайзере")
    public static void registerAction(String actionName) {
        new Http(orderServiceURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("action_name", actionName))
                .post("/v1/orders/actions");
    }

    public static Response changeProjectForOrderRequest(IProduct product, Project target) {
        return new Http(orderServiceURL)
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

    public static void switchProtect(String orderId, String projectId, boolean value) {
        Assertions.assertEquals(!value, new Http(orderServiceURL)
                .disableAttachmentLog()
                .setRole(CLOUD_ADMIN)
                .body(new JSONObject().put("order", new JSONObject().put("deletable", !value)))
                .patch("/v1/projects/{}/orders/{}", projectId, orderId)
                .assertStatus(200)
                .jsonPath()
                .getBoolean("deletable"));
    }

    @Step("Выполнение action {action.name} у заказа {action.orderId}")
    public static void runAction(ActionParameters action) {
        Assertions.assertNotEquals("locked", getStatusOrder(action.getOrderId(), action.getProjectId()),
                StringUtils.format("Статус заказа 'Заблокирован'. Действие '{}' не может быть выполнено", action.getName()));
        waitStatus(Duration.ofMinutes(10), action.getOrderId(), action.getProjectId());
        updateItemIdByOrderIdAndActionTitle(action);
        AtomicReference<Float> costPreBilling = new AtomicReference<>();
        AtomicReference<String> actionId = new AtomicReference<>();

        Assertions.assertAll("Проверка выполнения action - " + action.getName() + " у продукта " + action.getOrderId(),
                () -> {
                    if (action.getCheckPrebilling()) {
                        if (action.getSkipOnPrebilling())
                            costPreBilling.set(CalcCostSteps.getCostByUid(action.getOrderId(), action.getProjectId()));
                        else costPreBilling.set(CostSteps.getCostAction(action));
                        Assertions.assertTrue(costPreBilling.get() >= 0, "Стоимость после action отрицательная");
                    }
                },
                () -> {
                    actionId.set(sendAction(action).assertStatus(200).jsonPath().get("action_id"));
                    waitStatus(action.getTimeout(), action.getOrderId(), action.getProjectId());
                    waitStatusAction(Duration.ofMinutes(1), action.getOrderId(), actionId.get(), action.getProjectId());
                    checkActionStatusMethod(action.getOrderId(), action.getProjectId(), actionId.get());

                    if (Objects.nonNull(action.getStatus()))
                        action.getProduct().setStatus(action.getStatus());
                });

        if (costPreBilling.get() != null) {
            Float cost = null;
            for (int i = 0; i < 20; i++) {
                Waiting.sleep(20000);
                cost = CalcCostSteps.getCostByUid(action.getOrderId(), action.getProjectId());
                if (cost == null)
                    continue;
                if (Math.abs(cost - costPreBilling.get()) > 0.00001)
                    continue;
                break;
            }
            Assertions.assertNotNull(cost, "Стоимость списания равна null");
            Assertions.assertEquals(costPreBilling.get(), cost, 0.00001,
                    "Стоимость предбиллинга экшена отличается от стоимости списаний после action - " + action.getName());
        }
    }

    @Step("Ожидание успешного выполнения action")
    private static void checkActionStatusMethod(String orderId, String projectId, String actionId) {
        String actionStatus = getStatusAction(orderId, actionId, projectId);

        if (actionStatus.equalsIgnoreCase("warning")) {
            String messages = getActionHistoryOutput(orderId, projectId, actionId);
            Assertions.fail(String.format("Результат выполнения action продукта: warning. \nИтоговый статус: %s . \nОшибка: %s", actionStatus, messages));
        }
        if (!actionStatus.equalsIgnoreCase("success")) {
            String error = StateServiceSteps.getErrorFromStateService(projectId, orderId);
            if (Objects.isNull(error) || error.equals("[]"))
                error = "Действие не выполнено по таймауту";
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", orderId, actionStatus, error));
        }
    }

    @Step("Ожидание финального статуса по orderId = {orderId}")
    private static void waitStatus(Duration timeout, String orderId, String projectId) {
        Instant startTime = Instant.now();
        String status;
        do {
            Waiting.sleep(20000);
            status = getStatusOrder(orderId, projectId);
        } while ((status.equals("pending") || status.equals("changing")|| status.equals("removing") || status.isEmpty())
                && Duration.between(startTime, Instant.now()).compareTo(timeout) < 0);
        log.info("Финальный статус {}", status);
    }

    @Step("Ожидание warning по orderId = {orderId}")
    private static void waitStatusAction(Duration timeout, String orderId, String actionId, String projectId) {
        Instant startTime = Instant.now();
        String status;
        do {
            Waiting.sleep(20000);
            status = getStatusAction(orderId, actionId, projectId);
        } while ((status.equals("pending") || status.isEmpty())
                && Duration.between(startTime, Instant.now()).compareTo(timeout) < 0);
        log.info("Финальный статус {}", status);
    }

    @Step("Получение warning по orderId = {orderId}")
    public static String getActionHistoryOutput(String orderId, String projectId, String actionId) {
        return new Http(orderServiceURL)
                .setProjectId(projectId, Role.ORDER_SERVICE_ADMIN)
                .get("/v1/projects/{}/orders/{}/actions/history/{}/output",
                        projectId,
                        orderId,
                        actionId)
                .assertStatus(200)
                .jsonPath()
                .getString("list.collect{e -> e}.data");
    }

    @Step("Получение домена для сегмента сети")
    public static String getDomain(IProduct product) {
        Organization organization = Organization.builder().type("default").build().createObject();
        return new Http(orderServiceURL)
                .setProjectId(product.getProjectId(), Role.ORDER_SERVICE_ADMIN)
                .get("/v1/domains?net_segment_code={}&organization={}&with_restrictions=true&product_name={}&page=1&per_page=25",
                        product.getSegment(),
                        organization.getName(),
                        product.getProductCatalogName())
                .assertStatus(200)
                .jsonPath()
                .get("list.collect{e -> e}.shuffled()[0].code");
    }

    @Step("Получение домена для проекта {project}")
    public static String getDomainByProject(String project) {
        Organization organization = Organization.builder().type("default").build().createObject();
        if (Configure.ENV.equals("ift")) {
            return new Http(orderServiceURL)
                    .setRole(ORDER_SERVICE_ADMIN)
                    .get("/v1/domains?project_name={}&with_deleted=false&page=1&per_page=25&organzation={}", project, organization.getName())
                    .assertStatus(200)
                    .jsonPath()
                    .get("list.find{it.code=='corp.dev.vtb'}.code");
        } else {
            return new Http(orderServiceURL)
                    .setRole(ORDER_SERVICE_ADMIN)
                    .get("/v1/domains?project_name={}&with_deleted=false&page=1&per_page=25&organzation={}", project, organization.getName())
                    .assertStatus(200)
                    .jsonPath()
                    .get("list.collect{e -> e}.shuffled()[0].code");
        }
    }

    public static String getDataCentre(IProduct product) {
        String dc = "50";
        log.info("Получение ДЦ для сегмента сети {}", product.getSegment());
        Organization org = Organization.builder().type("default").build().createObject();
        List<String> list = new Http(orderServiceURL)
                .setProjectId(product.getProjectId(), Role.ORDER_SERVICE_ADMIN)
                .get("/v1/data_centers?net_segment_code={}&organization={}&with_restrictions=true&product_name={}&project_name={}&page=1&per_page=25",
                        product.getSegment(),
                        org.getName(),
                        product.getProductCatalogName(),
                        product.getProjectId())
                .assertStatus(200)
                .jsonPath()
                .getList("list.findAll{it.status == 'available'}.code");
        if (list.contains(dc))
            return dc;
        Assertions.assertFalse(list.isEmpty(), "Список available ДЦ пуст");
        return list.get(new Random().nextInt(list.size()));
    }

    @Step("Получение зоны доступности для сегмента сети {product.segment}")
    public static String getAvailabilityZone(IProduct product) {
        Organization org = Organization.builder().type("default").build().createObject();
        List<String> list = new Http(orderServiceURL)
                .setProjectId(product.getProjectId(), Role.ORDER_SERVICE_ADMIN)
                .get("/v1/availability_zones?net_segment_code={}&organization={}&with_restrictions=true&product_name={}&project_name={}&page=1&per_page=25",
                        product.getSegment(),
                        org.getName(),
                        product.getProductCatalogName(),
                        product.getProjectId())
                .assertStatus(200)
                .jsonPath()
                .getList("list.code");
        Assertions.assertFalse(list.isEmpty(), "Список зон доступности пуст");
        return list.get(new Random().nextInt(list.size()));
    }

    @Step("Получение платформы для зоны доступности {product.availabilityZone} и сегмента {product.segment}")
    public static String getPlatform(IProduct product) {
        String platform = "OpenStack";
        Organization org = Organization.builder().type("default").build().createObject();
        List<String> list = new Http(orderServiceURL)
                .setProjectId(product.getProjectId(), Role.ORDER_SERVICE_ADMIN)
                .get("/v1/platforms?net_segment_code={}&availability_zone_code={}&organization={}&with_restrictions=true&product_name={}&page=1&per_page=25",
                        product.getSegment(),
                        product.getAvailabilityZone(),
                        org.getName(),
                        product.getProductCatalogName())
                .assertStatus(200)
                .jsonPath()
                .getList("list.code");
        if (list.contains(platform))
            return platform;
        return list.get(new Random().nextInt(list.size()));
    }


    private static void updateItemIdByOrderIdAndActionTitle(ActionParameters action) {
        log.info("Получение item_id для " + Objects.requireNonNull(action.getName()));
        JsonPath jsonPath = new Http(orderServiceURL)
                .setProjectId(action.getProjectId(), ORDER_SERVICE_ADMIN)
                .get("/v1/projects/" + action.getProjectId() + "/orders/" + action.getOrderId())
                .assertStatus(200)
                .jsonPath();

        if (!action.getFilter().isEmpty())
            action.setFilter("it.data.config." + action.getFilter() + " && ");
        action.setItemId(jsonPath.getString(String.format("data.find{%sit.actions.find{it.name=='%s'}}.item_id", action.getFilter(), action.getName())));

        StringJoiner actions = new StringJoiner("\n", "\n", "");
        List<Map<String, Object>> mapList = jsonPath.getList("data.actions.flatten()");
        for (Map<String, Object> e : mapList)
            if (Objects.nonNull(e))
                actions.add(String.format("['%s' : '%s']", e.get("title"), e.get("name")));
        Assertions.assertNotNull(action.getItemId(), "Action '" + action.getName() + "' не найден\n Найденные экшены: " + actions);
        action.setSkipOnPrebilling(jsonPath.getBoolean(String.format("data.find{%sit.actions.find{it.name=='%s'}}.actions.find{it.name=='%s'}.skip_on_prebilling",
                action.getFilter(), action.getName(), action.getName())));
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
        JsonPath jsonPath = new Http(orderServiceURL)
                .disableAttachmentLog()
                .setProjectId(Objects.requireNonNull(product).getProjectId(), ORDER_SERVICE_ADMIN)
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
        String jsonArray = new Http(orderServiceURL)
                .setProjectId(projectId, ORDER_SERVICE_ADMIN)
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

    @Deprecated
    public static <T extends Comparable<T>> Comparable<T> getProductsField(IProduct product, String path) {
        return getProductsField(product, path, null);
    }

    @Deprecated
    public static <T> T getProductsField(IProduct product, String path, Class<T> clazz) {
        return getProductsField(product, path, clazz, true);
    }

    @Deprecated
    @Step("Получение значения по пути {path}")
    public static <T> T getProductsField(IProduct product, String path, Class<T> clazz, boolean assertion) {
        Object s;
        log.info("getFiledProduct path: " + path);
        JsonPath jsonPath = new Http(orderServiceURL)
                .disableAttachmentLog()
                .setProjectId(product.getProjectId(), ORDER_SERVICE_ADMIN)
                .get("/v1/projects/{}/orders/{}", Objects.requireNonNull(product).getProjectId(), product.getOrderId())
                .assertStatus(200)
                .jsonPath();
        if (Objects.nonNull(clazz))
            s = jsonPath.getObject(path, clazz);
        else
            s = jsonPath.get(path);
        log.info(String.format("getFiledProduct return: %s", s));
        if (assertion) {
            Assertions.assertNotNull(s, "По path '" + path + "' не найден объект в response " + jsonPath.prettify());
        }
        return (T) s;
    }

    private static JsonPath getObjectClass(IProduct product){
        return getObjectClass(Objects.requireNonNull(product).getProjectId(), product.getOrderId());
    }

    @Step("Получение объекта класса по пути {path}")
    private static JsonPath getObjectClass(String projectId, String orderId) {
        return new Http(orderServiceURL)
                .setProjectId(projectId, ORDER_SERVICE_ADMIN)
                .get("/v1/projects/{}/orders/{}", projectId, orderId)
                .assertStatus(200)
                .jsonPath();
    }

    public static <T> T getObjectClass(IProduct product, String path, TypeRef<T> valueTypeRef) {
        return getObjectClass(product).getObject(path, valueTypeRef);
    }

    public static <T> T getObjectClass(IProduct product, String path, Class<T> clazz) {
        return getObjectClass(product).getObject(path, clazz);
    }

    public static <T> T getObjectClass(String projectId, String orderId, String path, TypeRef<T> valueTypeRef) {
        return getObjectClass(projectId, orderId).getObject(path, valueTypeRef);
    }

    public static <T> T getObjectClass(String projectId, String orderId, String path, Class<T> clazz) {
        return getObjectClass(projectId, orderId).getObject(path, clazz);
    }

    @Step("Получение сетевого сегмента для продукта {product}")
    public static String getNetSegment(IProduct product) {
        String segment = "dev-srv-app";
        List<String> list = new Http(orderServiceURL)
                .setProjectId(product.getProjectId(), ORDER_SERVICE_ADMIN)
                .get("/v1/net_segments?project_name={}&with_restrictions=true&product_name={}&page=1&per_page=25",
                        Objects.requireNonNull(product).getProjectId(), product.getProductCatalogName())
                .assertStatus(200)
                .jsonPath()
                .getList("list.findAll{it.status == 'available'}.code");
        if (list.contains(segment))
            return segment;
        Assertions.assertFalse(list.isEmpty(), "Список available Segment пуст");
        return list.get(0);
    }

    @Step("Удаление всех заказов")
    public static void deleteOrders(String env, Predicate<String> label) {
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(Objects.requireNonNull(env)))
                .isForOrders(true).build().createObject();
        List<String> orders = new Http(orderServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/projects/{}/orders?include=total_count&page=1&per_page=100&f[status][]=success", project.id)
                .assertStatus(200)
                .jsonPath()
                .get("list.findAll{it.status == 'success'}.id");
        log.trace("list = " + orders);
        for (String order : orders) {
            try {
                JsonPath jsonPath = new Http(orderServiceURL)
                        .setRole(Role.CLOUD_ADMIN)
                        .get("/v1/projects/" + project.id + "/orders/" + order)
                        .jsonPath();
                if (!label.test(jsonPath.getString("label")))
                    continue;
                String itemId = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.item_id");
                String action = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.actions.find{it.type == 'delete'}.name");
                log.trace("item_id = " + itemId);
                log.trace("action = " + action);

                if (project.getProjectEnvironmentPrefix().getEnvType().equalsIgnoreCase("prod")) {
                    OrderServiceSteps.switchProtect(order, project.id, false);
                }

                JsonHelper.getJsonTemplate("/actions/template.json")
                        .set("$.item_id", itemId)
                        .send(orderServiceURL)
                        .setRole(Role.CLOUD_ADMIN)
                        .patch("/v1/projects/{}/orders/{}/actions/{}", project.id, order, action)
                        .assertStatus(200);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (Configure.ENV.equalsIgnoreCase("IFT")) {
            orders = new Http(orderServiceURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .get("/v1/projects/{}/orders?include=total_count&page=1&per_page=100&f[status][]=success&f[status][]=locked&f[status][]=changing&f[status][]=damaged&f[status][]=failure&f[status][]=pending&f[status][]=error", project.id)
                    .assertStatus(200)
                    .jsonPath()
                    .get("list.findAll{!(it.status == 'success' && it.deletable == true)}.id");

            StringJoiner params = new StringJoiner("&order_ids[]=", "order_ids[]=", "");
            orders.forEach(params::add);

            new Http(orderServiceURL)
                    .setRole(Role.CLOUD_ADMIN)
                    .delete("/v1/orders?&force=false&{}", params.toString());
            log.trace("list = " + orders);
        }

    }

    public static void deleteProduct(IProduct product) {
        new Http(orderServiceURL)
                .delete("/v1/projects/{}/orders/{}", product.getProjectId(), product.getOrderId());
    }

    public static Response getProductOrderService(String name) {
        return new Http(orderServiceAdminURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/v1/order_restrictions?product_name={}", name);
    }
}
