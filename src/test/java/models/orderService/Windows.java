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

@Log4j2
@Builder
public class Windows extends Entity implements IProduct {
    public String env;
    public String segment;
    public String dataCentre;
    public String platform;
    public String osVersion;
    public String orderId;
    public String projectId;
    public String productId;
    @Builder.Default
    public String productName = "Windows";
    @Builder.Default
    public String status = "NOT_CREATED";
    @Builder.Default
    public boolean isDeleted = false;

    @Override
    public void order() {
        JsonHelper jsonHelper = new JsonHelper();
        Project project = cacheService.entity(Project.class)
                .setField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .setField("projectName", project.id)
                .getEntity();
        projectId = project.id;
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath array = jsonHelper.getJsonTemplate("/orders/" + productName.toLowerCase() + ".json")
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .send(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders")
                .assertStatus(201)
                .jsonPath();
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
    public String getProductName(){
        return productName;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String getEnv() {
        return env;
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "Windows {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                ", osVersion='" + osVersion + '\'' +
                '}';
    }
}
