package models.orderService;

import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import models.Entity;
import models.orderService.interfaces.IProduct;
import steps.orderService.OrderServiceSteps;

@Log4j2
@Builder
public class RabbitMq extends Entity implements IProduct {
    String env;
    String segment;
    String dataCentre;
    String platform;
    String orderId;
    String product = "RabbitMQ";
    public String status = "NOT_CREATED";
    public boolean isDeleted = false;
    public String projectId;

    @Override
    public void order() {
        final JsonHelper jsonHelper = new JsonHelper();
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        Project project = cacheService.entity(Project.class).setField("env", env).getEntity();
        log.info("Отправка запроса на создание заказа для " + product);
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/orders/" + product.toLowerCase() + ".json")
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.project_name", project.id)
                .send(OrderServiceSteps.URL)
                .post("order-service/api/v1/projects/" + project.id + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");


        orderServiceSteps.checkOrderStatus("success", this);


        status = "CREATED";
        cacheService.saveEntity(this);
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public void delete() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("delete_two_layer", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String toString() {
        return "RabbitMQ {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }

}
