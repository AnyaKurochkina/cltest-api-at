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

@Data
@Log4j2
@Builder
public class RabbitMq extends Entity implements IProduct {


    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    protected final JsonHelper jsonHelper = new JsonHelper();

    private static final String URL = Configurier.getInstance().getAppProp("host_kong");

    String env;
    String segment;
    String dataCentre;
    String platform;
    String orderId;
    String product = "RabbitMQ";

    public RabbitMq(String env, String segment, String dataCentre, String platform, String orderId) {
        this.env = env;
        this.segment = segment;
        this.dataCentre = dataCentre;
        this.platform = platform;
        this.orderId = orderId;
    }

    @Override
    public void order(String projectName) {
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
        RabbitMq rabbitMq = RabbitMq.builder().orderId(jsonPath.get("order_id")).build();
        cacheService.saveEntity(RabbitMq.class, rabbitMq);
    }

    @Override
    public void reset() {
        orderServiceSteps.executeAction("reset_vm", this.getClass());
    }

    @Override
    public void stop(String method) {
        orderServiceSteps.executeAction("stop_vm" + method, this.getClass());
    }

    @Override
    public void start() {
        orderServiceSteps.executeAction("start_vm", this.getClass());
    }

    @Override
    public void delete() {
        orderServiceSteps.executeAction("delete_two_layer", this.getClass());
    }
}
