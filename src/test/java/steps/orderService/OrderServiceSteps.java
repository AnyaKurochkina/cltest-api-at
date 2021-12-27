package steps.orderService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.helper.Http;
import core.helper.JsonHelper;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.InformationSystem;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.ResourcePool;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
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
import static core.helper.Configure.ProductCatalogURL;

@Log4j2
public class OrderServiceSteps extends Steps {

    public void checkOrderStatus(String exp_status, IProduct product) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String orderStatus = "";
        int counter = 60;

        log.info("Проверка статуса заказа");
        while ((orderStatus.equals("pending") || orderStatus.equals("") || orderStatus.equals("changing")) && counter > 0) {
            Waiting.sleep(30000);
            Http.Response res = new Http(OrderServiceURL)
                    .setProjectId(product.getProjectId())
                    .get("projects/{}/orders/{}", product.getProjectId(), product.getOrderId())
                    .assertStatus(200);
            orderStatus = res.jsonPath().get("status");
            log.info("orderId={} orderStatus={}", product.getOrderId(), orderStatus);
            counter = counter - 1;
        }
        log.info("Итоговый статус заказа " + orderStatus);
        if (!orderStatus.equals(exp_status.toLowerCase())) {
            String error = "null";
            try {
                error = stateServiceSteps.GetErrorFromStateService(product);
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
    public List<String> getProductsWithStatus(String projectId, String... statuses) {
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
            String endPoint = String.format("projects/%s/orders?include=total_count&page=" +
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
    public Http.Response sendAction(String action, IProduct product, JSONObject jsonData) {
        Item item = getItemIdByOrderIdAndActionTitle(action, product);
        return JsonHelper.getJsonTemplate("/actions/template.json")
                .set("$.item_id", item.getId())
                .set("$.order.data", jsonData)
                .send(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .patch("projects/{}/orders/{}/actions/{}", product.getProjectId(), product.getOrderId(), item.getName());
    }

    public Http.Response changeProjectForOrderRequest(IProduct product, Project target) {
        return new Http(OrderServiceURL)
                .body(new JSONObject(String.format("{target_project_name: \"%s\"}", target.getId())))
                .patch("projects/{}/orders/{}/change_project", product.getProjectId(), product.getOrderId());
    }

    @Step("Перенос продукта {product} в проект {target}")
    public void changeProjectForOrder(IProduct product, Project target) {
        changeProjectForOrderRequest(product, target)
                .assertStatus(200);
        product.setProjectId(target.getId());
    }

    public void executeAction(String action, IProduct product, JSONObject jsonData) {
        executeAction(action, product, jsonData, null);
    }

    /**
     * Метод выполняет экшен по его имени
     *
     * @param action   имя экшена
     * @param product  объект продукта
     * @param jsonData параметр дата в запросе, к примеру: "order":{"data":{}}}
     */
    @Step("Выполнение action \"{action}\"")
    public void executeAction(String action, IProduct product, JSONObject jsonData, ProductStatus status) {
        CostSteps costSteps = new CostSteps();
        CalcCostSteps calcCostSteps = new CalcCostSteps();
        //Получение item'ов для экшена
        Item item = getItemIdByOrderIdAndActionTitle(action, product);
        log.info("Отправка запроса на выполнение действия '{}' продукта {}", action, product);
        //TODO: Возможно стоит сделать более детальную проверку на значение

        AtomicReference<Float> costPreBilling = new AtomicReference<>();
        AtomicReference<String> actionId = new AtomicReference<>();

        Assertions.assertAll("Проверка выполнения action - " + item.getName() + " у продукта " + product.getOrderId(),
                () -> {
                    costPreBilling.set(costSteps.getCostAction(item.getName(), item.getId(), product, jsonData));
                    Assertions.assertTrue(costPreBilling.get() >= 0, "Стоимость после action отрицательная");
                },
                () -> actionId.set(sendAction(action, product, jsonData)
                        .assertStatus(200)
                        .jsonPath()
                        .get("action_id")),
                () -> {
                    checkActionStatusMethod("success", product, actionId.get());
                    if (Objects.nonNull(status))
                        product.setStatus(status);
                },
                () -> {
                    if (costPreBilling.get() != null) {
                        Float cost = null;
                        for (int i = 0; i < 15; i++) {
                            Waiting.sleep(20000);
                            cost = calcCostSteps.getCostByUid(product);
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
    public void checkActionStatusMethod(String exp_status, IProduct product, String action_id) {
        StateServiceSteps stateServiceSteps = new StateServiceSteps();
        String actionStatus = "";
        int counter = 20;
        log.info("Проверка статуса выполнения действия");
        while ((actionStatus.equals("pending") || actionStatus.equals("changing") || actionStatus.equals("")) && counter > 0) {
            Waiting.sleep(30000);
            actionStatus = new Http(OrderServiceURL)
                    .setProjectId(product.getProjectId())
                    .get("projects/{}/orders/{}/actions/history/{}", product.getProjectId(), product.getOrderId(), action_id)
                    .assertStatus(200)
                    .jsonPath()
                    .get("status");
            counter = counter - 1;
        }
        if (!actionStatus.equals(exp_status.toLowerCase())) {
            String error = null;
            try {
                error = stateServiceSteps.GetErrorFromStateService(product);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (Objects.isNull(error))
                error = "Действие не выполнено по таймауту";
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s", product, actionStatus, error));
        }
    }

    public String getDomainBySegment(IProduct product, String netSegment) {
        log.info("Получение домена для сегмента сети " + netSegment);
        return new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("domains?net_segment_code={}&include=total_count&page=1&per_page=25", netSegment)
                .assertStatus(200)
                .jsonPath()
                .get("list[0].code");
    }

    /**
     * @param product объект продукт наследуемый от абстрактного класса IProduct
     * @return - возвращаем ID проудкта
     */
    public String getProductId(IProduct product) {
        log.info("Получение id для продукта " + Objects.requireNonNull(product).getProductName());
        //Получение информационной сисетмы
        InformationSystem informationSystem = InformationSystem.builder().isForOrders(true).build().createObject();
        String product_id = "";

        //Получение среды проекта
        ProjectEnvironment projectEnvironment = ((Project) Project.builder().id(product.getProjectId())
                .build().createObject()).getProjectEnvironment();

        //Выполнение запроса
        //TODO: оптимизировать
        int total_count = new Http(ProductCatalogURL)
                .setProjectId(product.getProjectId())
                .get("products/?is_open=true&env={}&information_systems={}&page=1&per_page=100", projectEnvironment.envType.toLowerCase(), informationSystem.id)
                .assertStatus(200)
                .jsonPath()
                .get("meta.total_count");

        int countOfIteration = total_count / 100 + 1;
        for (int i = 1; i <= countOfIteration; i++) {
            //Выполнение запроса на получение id подукта
            product_id = new Http(ProductCatalogURL)
                    .setProjectId(product.getProjectId())
                    .get("products/?is_open=true&env={}&information_systems={}&page={}&per_page=100", projectEnvironment.envType.toLowerCase(), informationSystem.id, i)
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

    /**
     * @param action  экшен
     * @param product продукт
     * @return - возвращаем ID айтема
     */
    public Item getItemIdByOrderIdAndActionTitle(String action, IProduct product) {
        log.info("Получение item_id для " + Objects.requireNonNull(action));
        //Отправка запроса на получение айтема
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(Objects.requireNonNull(product).getProjectId())
                .get("projects/" + product.getProjectId() + "/orders/" + product.getOrderId())
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


    @Step("Получение списка ресурсных пулов для категории {category} и среды {env}")
    public List<ResourcePool> getResourcesPoolList(String category, String projectId) {
        String jsonArray = new Http(OrderServiceURL)
                .setProjectId(projectId)
                .get("products/resource_pools?category={}&project_name={}", category, projectId)
                .assertStatus(200)
                .toJson()
                .getJSONArray("list")
                .toString();
        Type type = new TypeToken<List<ResourcePool>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    public <T extends Comparable<T>> Comparable<T> getProductsField(IProduct product, String path) {
        Comparable<T> s;
        log.info("getFiledProduct path: " + path);
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(product.getProjectId())
                .get("projects/{}/orders/{}", Objects.requireNonNull(product).getProjectId(), product.getOrderId())
                .assertStatus(200)
                .jsonPath();
        s = jsonPath.get(path);
        log.info(String.format("getFiledProduct return: %s", s));
        Assertions.assertNotNull(s, "По path '" + path + "' не найден объект в response " + jsonPath.prettify());
        return s;
    }

    @Step("Удаление всех заказов")
    public void deleteOrders(String env) {
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(Objects.requireNonNull(env)))
                .isForOrders(true).build().createObject();
        List<String> orders = new Http(OrderServiceURL)
                .setProjectId(project.id)
                .get("projects/{}/orders?include=total_count&page=1&per_page=100&f[status][]=success", project.id)
                .assertStatus(200)
                .jsonPath()
                .get("list.findAll{it.status == 'success'}.id");
        log.trace("list = " + orders);
        for (String order : orders) {
            try {
                JsonPath jsonPath = new Http(OrderServiceURL)
                        .setProjectId(project.id)
                        .get("projects/" + project.id + "/orders/" + order)
                        .jsonPath();
                String itemId = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.item_id");
                String action = jsonPath.get("data.find{it.actions.find{it.type == 'delete'}}.actions.find{it.type == 'delete'}.name");
                log.trace("item_id = " + itemId);
                log.trace("action = " + action);

                JsonHelper.getJsonTemplate("/actions/template.json")
                        .set("$.item_id", itemId)
                        .send(OrderServiceURL)
                        .setProjectId(project.id)
                        .patch("projects/{}/orders/{}/actions/{}", project.id, order, action)
                        .assertStatus(200);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
