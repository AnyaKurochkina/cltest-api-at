package models.orderService.products;

import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.subModels.KafkaTopic;
import models.subModels.Role;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Builder
public class ApacheKafkaCluster extends Entity implements IProduct{
    String env;
    String segment;
    String dataCentre;
    String platform;
    String orderId;
    String kafkaVersion;
    public String productId;
    public String domain;
    @Builder.Default
    String productName = "Apache Kafka Cluster";
    @Builder.Default
    public String status = "NOT_CREATED";
    @Builder.Default
    public boolean isDeleted = false;
    public String projectId;
    @Builder.Default
    public List<KafkaTopic> topics = new ArrayList<>();


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
    public String getEnv() {
        return env;
    }

    @Override
    public String getProductId() {
        return productId;
    }

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
        JsonPath jsonPath = jsonHelper.getJsonTemplate("/orders/apache_kafka_cluster.json")
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.attrs.kafka_version", kafkaVersion)
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
    public void delete() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Удалить рекурсивно", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void restart() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Перезагрузить кластер Kafka", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void stopSoft() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Выключить кластер Kafka", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void updateCerts() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Обновить сертификаты", "{\"dumb\":\"empty\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void createTopic(String name) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        KafkaTopic topic = new KafkaTopic("delete", 1,1,1, 1800000, name);
        String actionId = orderServiceSteps.executeAction("Создать Topic Kafka", cacheService.toJson(topic), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.add(topic);
        cacheService.saveEntity(this);
    }

    public void deleteTopic(String name) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Удалить Topic Kafka", "{\"topic_name\": \""+ name +"\"}",this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        Assert.assertFalse((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.removeIf(topic -> topic.getTopicName().equals(name));
        cacheService.saveEntity(this);
    }

    public void createAcl(String topicNameRegex) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Создать ACL Kafka", "{\"client_cn\":\"cnClient\",\"topic_type\":\"all_topics\",\"client_role\":\"consumer\",\"topic_name\":\""+topicNameRegex+"\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        cacheService.saveEntity(this);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_ACL, topicNameRegex)));
    }

    @Override
    public void start() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("Включить кластер Kafka", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void expand_mount_point() {
        IProduct.super.expand_mount_point();
    }

    @Override
    public String toString() {
        return "Apache Kafka Cluster {" +
                "env='" + env + '\'' +
                ", segment='" + segment + '\'' +
                ", dataCentre='" + dataCentre + '\'' +
                ", platform='" + platform + '\'' +
                ", kafka_Version" + kafkaVersion + '\'' +
                '}';
    }
}
