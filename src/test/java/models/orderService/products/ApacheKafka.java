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

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class ApacheKafka extends IProduct {
    String segment;
    String dataCentre;
    String platform;
    String kafkaVersion;
    String domain;
    public String status = "NOT_CREATED";
    final String jsonTemplate = "/orders/apache_kafka.json";
    final String productName = "Apache Kafka";
    public boolean isDeleted = false;

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
                .set("$.order.attrs.kafka_version", kafkaVersion)
                .set("$.order.project_name", project.id)
                .build();
    }

    @Override
    public void delete() {
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }


}
