package steps.tarifficator;

import core.helper.Configurier;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
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

    @Step("Получение предварительной стоимости заказываемого продукта")
    public void getCost(int cpus, int ram, String configCoreRamName, int diskSize,
                        String segment, String dataCentre, String platform,
                        String osVersion, String env, String productName,
                        String domain, String productId) {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        log.info("Отправка запроса на получение заказа для " + productName);
        JsonPath response = jsonHelper.getJsonTemplate("/tarifficator/cost.json")
                .set("$.params.flavor.cpus", cpus)
                .set("$.params.flavor.memory", ram)
                .set("$.params.flavor.name", configCoreRamName)
                .set("$.params.domain", domain)
                .set("$.params.extra_mounts.[0].size", diskSize)
                .set("$.params.default_nic.net_segment", segment)
                .set("$.params.data_center", dataCentre)
                .set("$.params.platform", platform)
                .set("$.params.os_version", osVersion)
                .set("$.params.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.project_name", project.id)
                .set("$.product_id", productId)
                .send(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .patch("tarifficator/api/v1/cost")
                .assertStatus(200)
                .jsonPath();
    }
}
