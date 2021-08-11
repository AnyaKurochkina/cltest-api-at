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

    @Step("Получение предварительной стоимости продукта Rhel с параметрами: {segment}, {dataCentre}, {platform}, {osVersion}, {env}, {productName}, {domain}")
    public void getCost(
                        String segment, String dataCentre, String platform,
                        String osVersion, String env, String productName,
                        String domain) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
//        Rhel rhel = cacheService.entity(Rhel.class).getEntity();
//        String productId = orderServiceSteps.getProductId(rhel);
        log.info("Отправка запроса на получение стоимости заказа для " + productName);
        JsonPath response = jsonHelper.getJsonTemplate("/tarifficator/cost.json")
                .set("$.params.domain", domain)
                .set("$.params.default_nic.net_segment", segment)
                .set("$.params.data_center", dataCentre)
                .set("$.params.platform", platform)
                .set("$.params.os_version", osVersion)
                .set("$.params.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.product_id", "c422069e-8f01-4328-b9dc-4a9e5dafd44e")
                .send(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("tarifficator/api/v1/cost")
                .assertStatus(200)
                .jsonPath();
    }
}
