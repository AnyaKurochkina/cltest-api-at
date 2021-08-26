package models.orderService.products;

import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;


@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public class ApacheKafkaCluster extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    @ToString.Include
    String kafkaVersion;
    String domain;
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
        setStatus(ProductStatus.CREATED);
        cacheService.saveEntity(this);
    }

    public ApacheKafkaCluster() {
        jsonTemplate = "/orders/apache_kafka_cluster.json";
        productName = "Apache Kafka Cluster";
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
                //.set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.attrs.kafka_version", kafkaVersion)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", env.toUpperCase().contains("TEST"))
                .build();
    }

    @Action("Удалить рекурсивно")
    public void delete(String action) {
        super.delete(action);
    }

    @Override
    @Action("Перезагрузить кластер Kafka")
    public void restart(String action) {
        super.restart(action);
    }

    @Override
    @Action("Выключить кластер Kafka")
    public void stopSoft(String action) {
        super.stopSoft(action);
    }

    @Action("Обновить сертификаты")
    public void updateCerts(String action) {
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{\"dumb\":\"empty\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    public void createTopic(String name, String action) {
        KafkaTopic topic = new KafkaTopic("delete", 1, 1, 1, 1800000, name);
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject(cacheService.toJson(topic)));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.add(topic);
        cacheService.saveEntity(this);
    }

    @Action("Создать Topic Kafka")
    public void createTopicTest(String action) {
        createTopic("TopicName", action);
    }

    public void deleteTopic(String name, String action) {
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{\"topic_name\": \"" + name + "\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        Assert.assertFalse((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.removeIf(topic -> topic.getTopicName().equals(name));
        cacheService.saveEntity(this);
    }

    @Action("Удалить Topic Kafka")
    public void deleteTopicTest(String action) {
        deleteTopic("TopicName", action);
    }

    public void createAcl(String topicNameRegex, String action) {
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{\"client_cn\":\"cnClient\",\"topic_type\":\"all_topics\",\"client_role\":\"consumer\",\"topic_name\":\"" + topicNameRegex + "\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        cacheService.saveEntity(this);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, topicNameRegex)));
    }

    public void createAclTransaction(String transactionRegex, String action) {
        String actionId = orderServiceSteps.executeAction(action, this, new JSONObject("{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\""+ transactionRegex +"\"}"));
        orderServiceSteps.checkActionStatus("success", this, actionId);
        cacheService.saveEntity(this);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)));
    }

    @Action("Создать ACL Kafka")
    public void createAclTest(String action) {
        createAcl("*", action);
    }

    @Action("Создание ACL на транзакцию Kafka")
    public void createAclTransactionTest(String action) {
        createAclTransaction("*", action);
    }

    @Override
    @Action("Включить кластер Kafka")
    public void start(String action) {
        super.start(action);
    }

}
