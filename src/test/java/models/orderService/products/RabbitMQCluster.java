package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import org.json.JSONObject;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import static org.junit.Assert.assertEquals;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class RabbitMQCluster extends IProduct {
    static String RABBITMQ_USER = "data.find{it.type=='cluster'}.config.users[0]";
    String segment;
    String dataCentre;
    String platform;
    String domain;
    String role = "administrator";
    String status = "NOT_CREATED";
    boolean isDeleted = false;

    @Override
    public void order() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        projectId = project.id;
        productId = orderServiceSteps.getProductId(this);
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = new Http(OrderServiceSteps.URL)
                .setProjectId(project.id)
                .post("order-service/api/v1/projects/" + project.id + "/orders",
                        getJsonParametrizedTemplate())
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        status = "CREATED";
        cacheService.saveEntity(this);
    }

    @Override
    public void init() {
        jsonTemplate = "/orders/rabbitmq_cluster.json";
        if(productName == null)
            productName = "RabbitMQ Cluster";
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        switch (env){
            case ("TEST"):
                role = "manager";
                break;
            case ("DEV"):
                role = "administrator";
                break;
        }
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.attrs.web_console_grants[0].role", role)
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", env.toUpperCase().contains("TEST"))
                .build();
    }

    public void rabbitmqCreateUser() {
        String user = "testapiuser";
        String actionId = orderServiceSteps.executeAction("Создать пользователя RabbitMQ", this, new JSONObject(String.format("{rabbitmq_users: [{user: \"%s\", password: \"%s\"}]}", user, user)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String username = (String) orderServiceSteps.getFiledProduct(this, RABBITMQ_USER);
        assertEquals(user, username);
    }

    @Override
    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this, null);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    //TODO: надо переделать связку экшен + продукт и нормально оборачивать
    @Override
    public void runActionsBeforeOtherTests() {
        boolean x = true;
        try {
            expandMountPoint();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            rabbitmqCreateUser();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            restart();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            stopSoft();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            start();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        try {
            stopHard();
        } catch (Throwable e) {
            x = false;
            e.printStackTrace();
        }
        Assert.assertTrue(x);
    }

}
