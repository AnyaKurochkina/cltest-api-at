package steps.tarifficator;

import core.helper.Configure;
import core.helper.Http;
import core.utils.Waiting;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;
import steps.orderService.OrderServiceSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class CostSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    @Step("Получение суммы расхода для продуктов")
    public Float getConsumptionSumOfProducts(List<String> productsId) {
        Float consumptionOfOneProduct;
        float consumption = 0F;
        for (String product : productsId) {
            consumptionOfOneProduct = new Http(URL)
                    .get("calculator/orders/cost/?uuid__in=" + product)
                    .assertStatus(200)
                    .jsonPath()
                    .get("cost");
            if (consumptionOfOneProduct != null) {
                consumption += consumptionOfOneProduct;
            }
            log.info("Стоимость продукта : " + consumptionOfOneProduct);
        }
        consumption = consumption;
        log.debug("Сумма расходов по всем продуктам: " + consumption);
        return consumption;
    }

    @Step("Получение расхода для папки/проекта")
    public double getConsumptionByPath(String path) {
        double consumption = new Http(URL)
                .get("calculator/orders/cost/?folder__startswith=" + path)
                .assertStatus(200)
                .jsonPath()
                .getDouble("cost");
        log.info("Расход для папки/проекта: " + consumption);
        return consumption;
    }

    @Step("Получение текущего расхода для заказа")
    public Float getCurrentCost(IProduct product) {
        Assertions.assertSame(ProductStatus.CREATED, product.getStatus(), "Продукт " + product + " не был заказан");
        Float consumption = null;
        for (int i = 0; i < 15; i++) {
            Waiting.sleep(20000);
            consumption = new Http(URL)
                    .setProjectId(product.getProjectId())
                    .get("calculator/orders/cost/?uuid__in=" + product.getOrderId())
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
    public Float getPreBillingCost(IProduct product) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
//        Project project = cacheService.entity(Project.class)
//                .withField("env", product.getEnv())
//                .forOrders(true)
//                .getEntity();
        Project project = Project.builder()
                .projectEnvironment(new ProjectEnvironment(product.getEnv()))
                .isForOrders(true)
                .build()
                .createObject();
//        product.setProjectId(project.getId());
        String productId = orderServiceSteps.getProductId(product);
        log.info("Отправка запроса на получение стоимости заказа для " + product.getProductName());
        JSONObject template = jsonHelper.getJsonTemplate("/tarifficator/cost.json").build();
        JSONObject attrs = (JSONObject) product.toJson().query("/order/attrs");
        template.put("params", attrs);
        template.put("project_name", project.id);
        template.put("product_id", productId);

        JsonPath response = new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("tarifficator/api/v1/cost", template)
                .assertStatus(200)
                .jsonPath();

        //TODO: Добавить проверки

        return response.get("total_price");
    }

    @Step("Получение предварительной стоимости продукта {product}")
    public JSONArray getCost(IProduct product) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        Project project = Project.builder()
                .projectEnvironment(new ProjectEnvironment(product.getEnv()))
                .isForOrders(true)
                .build()
                .createObject();
        String productId = orderServiceSteps.getProductId(product);
        log.info("Отправка запроса на получение стоимости заказа для " + product.getProductName());
        JSONObject template = jsonHelper.getJsonTemplate("/tarifficator/cost.json").build();
        JSONObject attrs = (JSONObject) product.toJson().query("/order/attrs");
        template.put("params", attrs);
        template.put("project_name", project.id);
        template.put("product_id", productId);

        return new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("tarifficator/api/v1/cost", template)
                .assertStatus(200)
                .toJson()
                .getJSONArray("items");
    }

    @Step("Получение предварительной стоимости action {action} продукта {product}")
    public Float getCostAction(String action, String itemId, IProduct product, JSONObject data) {
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(product.getEnv()))
                .isForOrders(true).build().createObject();
        log.info("Отправка запроса на получение стоимости экшена: "+ action +", у продукта " + product.getProductName());
        return jsonHelper.getJsonTemplate("/tarifficator/costAction.json")
                .set("$.params.project_name", project.id)
                .set("$.params.item_id", itemId)
                .set("$.params.action_name", action)
                .set("$.params.id", product.getOrderId())
                .set("$.params.order.data", data)
                .send(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("tarifficator/api/v1/cost")
                .assertStatus(200)
                .jsonPath()
                .get("total_price");
    }

    @Step("Сравниение тарифов заказываемого продукта с тарфиным планом")
    public void compareTariffs(HashMap<String, Double> activeTariffPlanPrice, JSONArray items) {
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
    public HashMap<String, Double> getPrices(String tariffPlanId) {
        JSONArray consumption = new Http(URL)
                .get("tarifficator/api/v1/tariff_plans/" + tariffPlanId + "?include=tariff_classes")
                .assertStatus(200)
                .toJson()
                .getJSONArray("tariff_classes");

        HashMap<String, Double> priceList = new HashMap<>();
        for (Object object : consumption) {
            priceList.put(((JSONObject) object).getString("name"), ((JSONObject) object).getDouble("price"));
        }
        System.out.println(priceList);
        return priceList;
    }

    @Step("Получение ID активного тарифного плана")
    public String getActiveTariffId() {
        return new Http(URL)
                .get("tarifficator/api/v1/tariff_plans?include=total_count&page=1&per_page=10&f[base]=false&f[organization_name]=vtb&sort=status&acc=up&f[status][]=active")
                .assertStatus(200)
                .jsonPath()
                .get("list[0].id");
    }

    public void generatePriceForState(HashMap<String, Double> priceListWithState, JSONArray items, String state) {
        JSONArray statusOnData = ((JSONObject) (items.get(0))).getJSONObject("resources_statuses").getJSONArray(state);
        for (Object object : statusOnData) {
            priceListWithState.put(((JSONObject) object).getString("name"), ((JSONObject) object).getDouble("price"));
        }
    }

    public void comparePrices(HashMap<String, Double> priceListWithState, HashMap<String, Double> activeTariffPlanPrice) {
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
