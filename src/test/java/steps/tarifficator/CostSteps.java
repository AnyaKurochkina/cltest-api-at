package steps.tarifficator;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import models.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;
import steps.orderService.OrderServiceSteps;
import steps.productCatalog.ProductCatalogSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static core.helper.Configure.CalculatorURL;
import static core.helper.Configure.TarifficatorURL;

@Log4j2
public class CostSteps extends Steps {

    @Step("Получение суммы расхода для продуктов")
    public static Float getConsumptionSumOfProducts(List<String> productsId) {
        Float consumptionOfOneProduct;
        float consumption = 0F;
        for (String product : productsId) {
            consumptionOfOneProduct = new Http(CalculatorURL)
                    .setRole(Role.ORDER_SERVICE_ADMIN)
                    .get("/orders/cost/?uuid__in={}", product)
                    .assertStatus(200)
                    .jsonPath()
                    .get("cost");
            if (consumptionOfOneProduct != null) {
                consumption += consumptionOfOneProduct;
            }
            log.info("Стоимость продукта : " + consumptionOfOneProduct);
        }
        log.debug("Сумма расходов по всем продуктам: " + consumption);
        return consumption;
    }

    @Step("Получение расхода для папки/проекта")
    public static double getConsumptionByPath(String path) {
        double consumption = new Http(CalculatorURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get("/orders/cost/?folder__startswith={}", path)
                .assertStatus(200)
                .jsonPath()
                .getDouble("cost");
        log.info("Расход для папки/проекта: " + consumption);
        return consumption;
    }

    @Step("Получение текущего расхода для заказа")
    public static Float getCurrentCost(IProduct product) {
        Assertions.assertNotSame(null, product.getStatus(), "Продукт " + product + " не был заказан");
        Float consumption = null;
        for (int i = 0; i < 15; i++) {
            Waiting.sleep(20000);
            consumption = new Http(CalculatorURL)
                    .setProjectId(product.getProjectId())
                    .get("/orders/cost/?uuid__in={}", product.getOrderId())
                    .assertStatus(200)
                    .jsonPath()
                    .get("cost");
            if (consumption == null) {
                continue;
            }
            if (consumption == 0.0f) {
                continue;
            }
            break;
        }
        Assertions.assertNotNull(consumption, "Расход заказа равна null");
        log.debug("Расход для заказа: " + consumption);
        return consumption;
    }

    @Step("Получение предварительной стоимости продукта {product}")
    public static Float getPreBillingTotalCost(IProduct product) {
        return getPreBillingCostPath(product, "total_price");
    }

    public static Float getPreBillingCostPath(IProduct product, String path) {
        Project project = Project.builder()
                .projectEnvironmentPrefix(new ProjectEnvironmentPrefix(product.getEnv()))
                .isForOrders(true)
                .build()
                .createObject();
        String productId = new ProductCatalogSteps("/api/v1/products/")
                .getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(product.getProductName(),
                        "is_open=true&env=" + Objects.requireNonNull(project.getProjectEnvironmentPrefix().getEnvType().toLowerCase()));
        log.info("Отправка запроса на получение стоимости заказа для " + product.getProductName());
        JSONObject template = JsonHelper.getJsonTemplate("/tarifficator/cost.json").build();
        JSONObject attrs = (JSONObject) product.toJson().query("/order/attrs");

        template.getJSONObject("order").put("attrs", attrs);
//        template.put("params", attrs);
        template.put("project_name", project.id);
        template.getJSONObject("order").put("product_id", productId);

        if (Objects.nonNull(product.getOrderId())) {
            template = JsonHelper.getJsonTemplate("/tarifficator/costItems.json").build();
            JSONObject vm = new JSONObject((Map) OrderServiceSteps.getProductsField(product, "", Map.class));
            template.put("tariff_plan_id", vm.query("/attrs/tariff_plan_id"));

            JSONArray items = (JSONArray) vm.query("/data");
            for (Object itemObj : items) {
                JSONObject item = (JSONObject) itemObj;
                JSONObject costItem = new JSONObject();
                costItem.put("item_id", item.get("item_id"));
                costItem.put("type", item.get("type"));
                costItem.put("data", item.get("data"));
                template.append("items", costItem);
            }
            return new Http(TarifficatorURL)
                    .setProjectId(project.id)
                    .body(template)
                    .post("/v1/cost_items")
                    .assertStatus(200)
                    .jsonPath()
                    .get(path);
        }

        JsonPath response = new Http(TarifficatorURL)
                .setProjectId(project.id)
                .body(template)
                .post("/v1/cost")
                .assertStatus(200)
                .jsonPath();

        //TODO: Добавить проверки

        return response.get(path);
    }

    @Step("Получение предварительной стоимости продукта {product}")
    public static JSONArray getCost(IProduct product) {
        Project project = Project.builder()
                .projectEnvironmentPrefix(new ProjectEnvironmentPrefix(product.getEnv()))
                .isForOrders(true)
                .build()
                .createObject();
        String productId = new ProductCatalogSteps("/api/v1/products/")
                .getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(product.getProductName(),
                        "is_open=true&env=" + Objects.requireNonNull(project.getProjectEnvironmentPrefix().getEnvType()).toLowerCase());
        log.info("Отправка запроса на получение стоимости заказа для " + product.getProductName());
        JSONObject template = JsonHelper.getJsonTemplate("/tarifficator/cost.json").build();
        JSONObject attrs = (JSONObject) product.toJson().query("/order/attrs");
        template.getJSONObject("order").put("attrs", attrs);
        template.put("project_name", project.id);
        template.getJSONObject("order").put("product_id", productId);

        return new Http(TarifficatorURL)
                .setProjectId(project.id)
                .body(template)
                .post("/v1/cost")
                .assertStatus(200)
                .toJson()
                .getJSONArray("items");
    }

    @Step("Получение предварительной стоимости action {action} продукта {product}")
    public static Float getCostAction(String action, String itemId, IProduct product, JSONObject data) {
//        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(product.getEnv()))
//                .isForOrders(true).build().createObject();
//        Project project = Project.builder().id(product.getProjectId()).build().createObject();
        log.info("Отправка запроса на получение стоимости экшена: " + action + ", у продукта " + product.getProductName());
        return JsonHelper.getJsonTemplate("/tarifficator/costAction.json")
                .set("$.project_name", product.getProjectId())
                .set("$.item_id", itemId)
                .set("$.action_name", action)
                .set("$.id", product.getOrderId())
                .set("$.order.attrs", data)
                .send(TarifficatorURL)
                .setProjectId(product.getProjectId())
                .post("/v1/cost")
                .assertStatus(200)
                .jsonPath()
                .get("total_price");
    }

    @Step("Сравниение тарифов заказываемого продукта с тарфиным планом")
    public static void compareTariffs(HashMap<String, Double> activeTariffPlanPrice, JSONArray items) {
        //Создаем 3 прайса ON, OFF, REBOOT
        HashMap<String, Double> priceListOn = new HashMap<>();
        HashMap<String, Double> priceListOff = new HashMap<>();
        HashMap<String, Double> priceListReboot = new HashMap<>();
        //Наполняем прайс для состояния ON
        generatePriceForState(priceListOn, items, "on");
        //Наполняем прайс для состояния OFF
        generatePriceForState(priceListOff, items, "off");
        //Наполняем прайс для состояния REBOOT
        generatePriceForState(priceListReboot, items, "reboot");
        //Сравниваем цены в прайсе ON с активным тарифным планом
        comparePrices(priceListOn, activeTariffPlanPrice);
        //Сравниваем цены в прайсе OFF с активным тарифным планом
        comparePrices(priceListOff, activeTariffPlanPrice);
        //Сравниваем цены в прайсе REBOOT с активным тарифным планом
        comparePrices(priceListReboot, activeTariffPlanPrice);
    }

    @Step("Запрос цен по ID тарифного плана")
    public static HashMap<String, Double> getPrices(String tariffPlanId) {
        JSONArray consumption = new Http(TarifficatorURL)
                .setRole(Role.TARIFFICATOR_ADMIN)
                .get("/v1/tariff_plans/{}?include=tariff_classes", tariffPlanId)
                .assertStatus(200)
                .toJson()
                .getJSONArray("tariff_classes");

        HashMap<String, Double> priceList = new HashMap<>();
        for (Object object : consumption) {
            priceList.put(((JSONObject) object).getString("name"), ((JSONObject) object).getDouble("price"));
        }
        log.info(priceList);
        return priceList;
    }

    @Step("Получение ID активного тарифного плана")
    public static String getActiveTariffId() {
        return new Http(TarifficatorURL)
                .setRole(Role.TARIFFICATOR_ADMIN)
                .get("/v1/tariff_plans?include=total_count&page=1&per_page=10&f[base]=false&f[organization_name]=vtb&sort=status&acc=up&f[status][]=active")
                .assertStatus(200)
                .jsonPath()
                .get("list[0].id");
    }

    public static void generatePriceForState(HashMap<String, Double> priceListWithState, JSONArray items, String state) {
        JSONArray statusOnData = ((JSONObject) (items.get(0))).getJSONObject("resources_statuses").getJSONArray(state);
        for (Object object : statusOnData) {
            priceListWithState.put(((JSONObject) object).getString("name"), ((JSONObject) object).getDouble("price"));
        }
    }

    public static void comparePrices(HashMap<String, Double> priceListWithState, HashMap<String, Double> activeTariffPlanPrice) {
        for (Map.Entry<String, Double> entry : priceListWithState.entrySet()) {
            String preBillingServiceName = entry.getKey();
            for (Map.Entry<String, Double> entry2 : activeTariffPlanPrice.entrySet()) {
                String tariffPLanServiceName = entry2.getKey();
                if (preBillingServiceName.equals(tariffPLanServiceName)) {
                    Assertions.assertEquals(priceListWithState.get(preBillingServiceName), activeTariffPlanPrice.get(tariffPLanServiceName), 0.00000001,
                            "Цена услуги: " + preBillingServiceName + " в предбиллинге: " + priceListWithState.get(preBillingServiceName)
                                    + " Не соответствует цене услуги: " + tariffPLanServiceName + " в тарифном плане: " + activeTariffPlanPrice.get(tariffPLanServiceName));
                    log.info(
                            "Цена услуги: " + preBillingServiceName + " в предбиллинге: " + priceListWithState.get(preBillingServiceName)
                                    + " Соответствует цене услуги: " + tariffPLanServiceName + " в тарифном плане: " + activeTariffPlanPrice.get(tariffPLanServiceName));
                }
            }
        }
    }
}
