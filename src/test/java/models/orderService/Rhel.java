package models.orderService;

import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import models.Entity;
import models.orderService.interfaces.IProduct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import steps.orderService.OrderServiceSteps;

@Log4j2
@Builder
public class Rhel extends Entity implements IProduct {
    public String env;
    public String segment;
    public String dataCentre;
    public String platform;
    public String orderId;
    public String projectId;
    public String product = "Rhel";
    public String status = "NOT_CREATED";
    public boolean isDeleted = false;

    @Override
    public void order() {
        product = "Rhel";
        status = "NOT_CREATED";

        JsonHelper jsonHelper = new JsonHelper();
        Project project = cacheService.entity(Project.class).setField("env", env).getEntity();
        projectId = project.id;
        log.info("Отправка запроса на создание заказа для " + product);
        JsonPath array = jsonHelper.getJsonTemplate("/orders/" + product.toLowerCase() + ".json")
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.project_name", project.id)
                .send(OrderServiceSteps.URL)
                .post("order-service/api/v1/projects/" + project.id + "/orders")
                .assertStatus(201)
                .jsonPath();
        //orderId = (String) ((JSONObject) array.get(0)).get("order_id");
        orderId = array.get("[0].id");

        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        orderServiceSteps.checkOrderStatus("success", this);

        status = "CREATED";
        cacheService.saveEntity(this);

    }

    @Override
    public String getOrderId(){
        return orderId;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String toString() {
        return "Rhel {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
