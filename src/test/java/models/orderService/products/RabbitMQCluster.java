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
    String status = "NOT_CREATED";
    final String jsonTemplate = "/orders/rabbitmq_cluster.json";
    final String productName = "RabbitMQ Cluster";
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
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup.name)
                .set("$.order.project_name", project.id)
                .build();
    }

    public void rabbitmqCreateUser() {
        String user = "testapiuser";
        String actionId = orderServiceSteps.executeAction("Создать пользователя RabbitMQ", String.format("{rabbitmq_users: [{user: \"%s\", password: \"%s\"}]}", user, user), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String username = (String) orderServiceSteps.getFiledProduct(this, RABBITMQ_USER);
        assertEquals(user, username);
    }

    @Override
    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void runActionsBeforeOtherTests(){
        expandMountPoint();
        rabbitmqCreateUser();
        restart();
        stopSoft();
        start();
        stopHard();
    }

}
