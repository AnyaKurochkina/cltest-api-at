package models.orderService.products;

import core.CacheService;
import core.helper.Http;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.Action;
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.Arrays;
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
    Flavor flavor;

    public static final String KAFKA_CREATE_TOPIC = "Создать Topic Kafka";

    @Override
    public void order() {
        JSONObject template = getJsonParametrizedTemplate();
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = new Http(OrderServiceSteps.URL)
                .setProjectId(projectId)
                .post("order-service/api/v1/projects/" + projectId + "/orders", template)
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
                .forOrders(true)
                .getEntity();
        if(productId == null) {
            projectId = project.id;
            productId = orderServiceSteps.getProductId(this);
        }
        List<Flavor> flavorList = referencesStep.getProductFlavorsLinkedList(this);
        flavor = flavorList.get(0);
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                //.set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.name)
                .set("$.order.attrs.kafka_version", kafkaVersion)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", ((ProjectEnvironment) cacheService.entity(ProjectEnvironment.class).withField("env", project.env).getEntity()).envType.contains("TEST"))
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
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"dumb\":\"empty\"}"));
    }

    public void createTopic(KafkaTopic kafkaTopic) {
        orderServiceSteps.executeAction(KAFKA_CREATE_TOPIC, this, new JSONObject(CacheService.toJson(kafkaTopic)));
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, kafkaTopic.getTopicName())));
        topics.add(kafkaTopic);
        cacheService.saveEntity(this);
    }

    public void createTopic(List<String> names, String action) {
        List<KafkaTopic> kafkaTopics = new ArrayList<>();
        for(String name : names)
            kafkaTopics.add(new KafkaTopic("delete", 1, 1, 1, 1800000, name));
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"topics\": " + CacheService.toJson(kafkaTopics) + "}"));
        for(String name : names)
            Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.addAll(kafkaTopics);
        cacheService.saveEntity(this);
    }


    @Action(KAFKA_CREATE_TOPIC)
    public void createTopicTest(String action) {
        createTopic(new KafkaTopic("delete", 1, 1, 1, 1800000, "TopicName"));
    }

    @Action("Пакетное создание Topic-ов Kafka")
    public void createTopicsTest(String action) {
        createTopic(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"), action);
    }

    @Action("Пакетное удаление Topic-ов Kafka")
    public void deleteTopicsTest(String action) {
        deleteTopic(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"), action);
    }

    public void deleteTopic(List<String> names, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"topics\": " + CacheService.toJson(names) + "}"));
        for(String name : names)
            Assert.assertFalse((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        for(String name : names)
            topics.removeIf(topic -> topic.getTopicName().equals(name));
        cacheService.saveEntity(this);
    }

    public void deleteTopic(String name, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"topic_name\": \"" + name + "\"}"));
        Assert.assertFalse((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.removeIf(topic -> topic.getTopicName().equals(name));
        cacheService.saveEntity(this);
    }

    @Action("Удалить Topic Kafka")
    public void deleteTopicTest(String action) {
        deleteTopic("TopicName", action);
    }

    public void createAcl(String topicNameRegex, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"client_cn\":\"cnClient\",\"topic_type\":\"all_topics\",\"client_role\":\"consumer\",\"topic_name\":\"" + topicNameRegex + "\"}"));
        cacheService.saveEntity(this);
        Assert.assertTrue((Boolean) orderServiceSteps.getFiledProduct(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, topicNameRegex)));
    }

    public void createAclTransaction(String transactionRegex, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\""+ transactionRegex +"\"}"));
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
