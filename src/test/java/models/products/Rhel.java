package models.products;

import core.helper.Configurier;
import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import models.Authorizer.Project;
import models.Entity;
import models.interfaces.IProduct;
import stepsOld.OrderServiceSteps;

@Log4j2
@Data
@Builder
public class Rhel extends Entity implements IProduct {


    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    String env;
    String segment;
    String dataCentre;
    String platform;
    String orderId;
    String product = "Rhel";

    public Rhel(String env, String segment, String dataCentre, String platform) {
        this.env = env;
        this.segment = segment;
        this.dataCentre = dataCentre;
        this.platform = platform;
    }

    @Override
    public void order(String projectName) {
        JsonHelper jsonHelper = new JsonHelper();
        Project project = cacheService.entity(Project.class).setField("projectName", projectName).getEntity();
        String projectId = project.id;
        log.info("Отправка запроса на создание заказа для " + product);
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/orders/" + product.toLowerCase() + ".json")
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.project_name", projectId)
                .send(URL)
                .post("order-service/api/v1/projects/" + projectId + "/orders")
                .assertStatus(201)
                .jsonPath();
        Rhel rhel = Rhel.builder().orderId(jsonPath.get("order_id")).build();
        cacheService.saveEntity(Rhel.class, rhel);
    }

    @Override
    public void reset() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.executeAction("reset_vm");
    }

    @Override
    public void stop(String method) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.executeAction("stop_vm" + method);
    }

    @Override
    public void start() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.executeAction("start_vm");
    }

    @Override
    public void delete() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.executeAction("delete_vm");
    }
}
