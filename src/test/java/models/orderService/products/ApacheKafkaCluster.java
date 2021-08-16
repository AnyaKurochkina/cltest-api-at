package models.orderService.products;

import core.helper.Http;
import core.helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class ApacheKafkaCluster extends IProduct {
    String segment;
    String dataCentre;
    String platform;
    String kafkaVersion;
    String domain;
    public String status = "NOT_CREATED";
    public boolean isDeleted = false;
    final String jsonTemplate = "/orders/apache_kafka_cluster.json";
    final String productName = "Apache Kafka Cluster";
    public List<KafkaTopic> topics = new ArrayList<>();

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

    @Override
    public void restart() {
        String actionId = orderServiceSteps.executeAction("Перезагрузить кластер Kafka", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void stopSoft() {
        String actionId = orderServiceSteps.executeAction("Выключить кластер Kafka", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void updateCerts() {
        String actionId = orderServiceSteps.executeAction("Обновить сертификаты", "{\"dumb\":\"empty\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void createTopic(String name) {
        KafkaTopic topic = new KafkaTopic("delete", 1, 1, 1, 1800000, name);
        String actionId = orderServiceSteps.executeAction("Создать Topic Kafka", cacheService.toJson(topic), this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.add(topic);
        cacheService.saveEntity(this);
    }

    public void deleteTopic(String name) {
        String actionId = orderServiceSteps.executeAction("Удалить Topic Kafka", "{\"topic_name\": \"" + name + "\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        Assert.assertFalse((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.removeIf(topic -> topic.getTopicName().equals(name));
        cacheService.saveEntity(this);
    }

    public void createAcl(String topicNameRegex) {
        String actionId = orderServiceSteps.executeAction("Создать ACL Kafka", "{\"client_cn\":\"cnClient\",\"topic_type\":\"all_topics\",\"client_role\":\"consumer\",\"topic_name\":\"" + topicNameRegex + "\"}", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
        cacheService.saveEntity(this);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_ACL, topicNameRegex)));
    }

    @Override
    public void start() {
        String actionId = orderServiceSteps.executeAction("Включить кластер Kafka", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Override
    public void runActionsBeforeOtherTests(){
        updateCerts();
        createTopic("TopicName");
        createAcl("*");
        stopSoft();
        start();
        restart();
        expandMountPoint();
    }

    @Override
    public void runActionsAfterOtherTests(){
        deleteTopic("TopicName");
        delete();
    }

}
