package models.orderService.products;

import core.CacheService;
import core.helper.Http;
import io.qameta.allure.Step;
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
import org.junit.Assert;
import steps.orderService.OrderServiceSteps;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
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
    String osVersion;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = new Http(OrderServiceSteps.URL)
                .setProjectId(projectId)
                .post("order-service/api/v1/projects/" + projectId + "/orders", toJson())
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
    }

    @Override
    public void init() {
        jsonTemplate = "/orders/apache_kafka_cluster.json";
        productName = "Apache Kafka Cluster";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if(projectId == null) {
            projectId = project.getId();
        }
        if(productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        List<Flavor> flavorList = referencesStep.getProductFlavorsLinkedList(this);
        flavor = flavorList.get(0);
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.kafka_version", kafkaVersion)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .build();
    }

    public void createTopics(List<String> names) {
        List<KafkaTopic> kafkaTopics = new ArrayList<>();
        for(String name : names)
            kafkaTopics.add(new KafkaTopic("delete", 1, 1, 1, 1800000, name));
        orderServiceSteps.executeAction("kafka_create_topics", this, new JSONObject("{\"topics\": " + CacheService.toJson(kafkaTopics) + "}"));
        for(String name : names)
            Assert.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.addAll(kafkaTopics);
        save();
    }

    public void deleteTopics(List<String> names) {
        orderServiceSteps.executeAction("kafka_delete_topics", this, new JSONObject("{\"topics\": " + CacheService.toJson(names) + "}"));
        for(String name : names)
            Assert.assertFalse((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        for(String name : names)
            topics.removeIf(topic -> topic.getTopicName().equals(name));
        save();
    }

    public void createAcl(String topicNameRegex, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"client_cn\":\"cnClient\",\"topic_type\":\"all_topics\",\"client_role\":\"consumer\",\"topic_name\":\"" + topicNameRegex + "\"}"));
        save();
        Assert.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, topicNameRegex)));
    }

    public void createAclTransaction(String transactionRegex, String action) {
        orderServiceSteps.executeAction(action, this, new JSONObject("{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\""+ transactionRegex +"\"}"));
        save();
        Assert.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)));
    }

    public void start() {
        start("start_kafka");
    }

    public void updateCerts(){
        updateCerts("kafka_update_certs");
    }

    public void expandMountPoint(){
        expandMountPoint("expand_mount_point");
    }

    public void restart() {
        restart("restart_kafka");
    }

    public void stopSoft() {
        stopSoft("stop_kafka");
    }

    @Override
    protected void delete(){
        delete("delete_two_layer");
    }

}
