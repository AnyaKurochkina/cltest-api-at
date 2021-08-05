package models.orderService.products;

import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.Entity;
import models.orderService.interfaces.IProduct;
import steps.orderService.OrderServiceSteps;

import static org.junit.Assert.assertEquals;

@Log4j2
@Builder
public class RabbitMQCluster extends Entity implements IProduct {
    public static String RABBITMQ_USER = "data.find{it.type=='cluster'}.config.users[0]";
    public String env;
    public String segment;
    public String dataCentre;
    public String platform;
    public String orderId;
    public String productId;
    public String domain;
    @Builder.Default
    public String productName = "RabbitMQ Cluster";
    @Builder.Default
    public String status = "NOT_CREATED";
    @Builder.Default
    public boolean isDeleted = false;
    public String projectId;

    @Override
    public void order() {
        final JsonHelper jsonHelper = new JsonHelper();
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        projectId = project.id;
        productId = orderServiceSteps.getProductId(this);
        domain = orderServiceSteps.getDomainBySegment(this, segment);

        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/orders/" + productName.toLowerCase() + ".json")
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup.name)
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

    public void rabbitmq_create_user() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String user = "testapiuser";
        String actionId = orderServiceSteps.executeAction("Создать пользователя RabbitMQ", String.format("{rabbitmq_users: [{user: \"%s\", password: \"%s\"}]}", user, user), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String username = (String) orderServiceSteps.getFiledProduct(this, RABBITMQ_USER);
        assertEquals(user, username);
    }

    @Override
    public void delete() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }


    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getProductName() {
        return productName;
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
        return "RabbitMQ Cluster {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }

}
