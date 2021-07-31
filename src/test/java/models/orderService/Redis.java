package models.orderService;

import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import steps.orderService.OrderServiceSteps;

import static org.junit.Assert.assertTrue;

@Log4j2
@Builder
public class Redis extends Entity implements IProduct {

    String env;
    String segment;
    String dataCentre;
    String platform;
    String orderId;
    @Builder.Default
    String productName = "Redis";
    @Builder.Default
    public String status = "NOT_CREATED";
    @Builder.Default
    public boolean isDeleted = false;
    public String projectId;


    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public void order() {
        final JsonHelper jsonHelper = new JsonHelper();
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        Project project = cacheService.entity(Project.class)
                .setField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .setField("projectName", project.id)
                .getEntity();
        projectId = project.id;
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/orders/" + productName.toLowerCase() + ".json")
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .send(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");


        orderServiceSteps.checkOrderStatus("success", this);


        status = "CREATED";
        cacheService.saveEntity(this);
    }

    @Override
    public void reset() {
        IProduct.super.reset();
    }

    @Override
    public void stopHard() {
        IProduct.super.stopHard();
    }

    @Override
    public void stopSoft() {
        IProduct.super.stopSoft();
    }

    @Override
    public void start() {
        IProduct.super.start();
    }

    @Override
    public void delete() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void expand_mount_point() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        int sizeBefore = orderServiceSteps.getExpandMountSize(this);
        String actionId = orderServiceSteps.executeAction("Расширить", "{\"size\": 10, \"mount\": \"/app/redis/data\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        int sizeAfter = orderServiceSteps.getExpandMountSize(this);
        assertTrue(sizeBefore<sizeAfter);
    }

    @Override
    public String toString() {
        return "Redis {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
