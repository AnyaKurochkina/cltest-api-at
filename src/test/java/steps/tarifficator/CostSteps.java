package steps.tarifficator;

import core.helper.Configurier;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.orderService.products.Rhel;
import org.json.JSONObject;
import steps.Steps;
import steps.orderService.OrderServiceSteps;

@Log4j2
public class CostSteps extends Steps {
    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    @Step("Получение расхода для папки/проекта")
    public float getConsumptionByPath(String path) {
        float consumption = new Http(URL)
                .get("calculator/orders/cost/?folder__startswith=" + path)
                .assertStatus(200)
                .jsonPath()
                .get("cost");

        log.info("Расход для папки/проекта: " + consumption);
        return consumption * 24 * 60;
    }

    @Step("Получение предварительной стоимости продукта {product}")
    public void getCost(IProduct product) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        Project project = cacheService.entity(Project.class)
                .withField("env", product.getEnv())
                .getEntity();
        String productId = orderServiceSteps.getProductId(product);
        log.info("Отправка запроса на получение стоимости заказа для " + product.getProductName());
        JSONObject template = jsonHelper.getJsonTemplate("/tarifficator/cost.json").build();
        JSONObject attrs = jsonHelper.getJsonObjectFromFile(product.getJsonTemplate(), "/order/attrs");
        template.put("params", attrs);
        template.put("project_name", project.id);
        template.put("product_id", productId);

        JsonPath response = new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("tarifficator/api/v1/cost", template)
                .assertStatus(200)
                .jsonPath();

        System.out.println(response.getString("total_price"));
    }
}
