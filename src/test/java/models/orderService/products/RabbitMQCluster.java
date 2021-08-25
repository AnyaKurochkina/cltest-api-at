package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import org.json.JSONObject;
import org.junit.Action;
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
        setStatus(ProductStatus.CREATED);
        cacheService.saveEntity(this);
    }

    public RabbitMQCluster() {
        jsonTemplate = "/orders/rabbitmq_cluster.json";
        productName = "RabbitMQ Cluster";
    }

    @Override
    public JSONObject getJsonParametrizedTemplate() {
        Project project = cacheService.entity(Project.class)
                .withField("env", env)
                .getEntity();
        ProjectEnvironment projectEnvironment = cacheService.entity(ProjectEnvironment.class)
                .withField("env", env)
                .getEntity();
        AccessGroup accessGroup = cacheService.entity(AccessGroup.class)
                .withField("projectName", project.id)
                .getEntity();
        switch (projectEnvironment.envType){
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

    @Action("Создать пользователя RabbitMQ")
    public void rabbitmqCreateUser(String action) {
        String user = "testapiuser";
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(String.format("{rabbitmq_users: [{user: \"%s\", password: \"%s\"}]}", user, user)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        String username = (String) orderServiceSteps.getFiledProduct(this, RABBITMQ_USER);
        assertEquals(user, username);
    }

    @Override
    @Action("Удалить рекурсивно")
    public void delete(String action) {
        super.delete(action);
    }


}
