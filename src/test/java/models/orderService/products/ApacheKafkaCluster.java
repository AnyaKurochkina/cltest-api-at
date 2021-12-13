package models.orderService.products;

import core.CacheService;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@Data
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ApacheKafkaCluster extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    @ToString.Include
    String kafkaVersion;
    String domain;
    @Builder.Default
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
        compareCostOrderAndPrice();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/apache_kafka_cluster.json";
        productName = "Apache Kafka Cluster";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            projectId = project.getId();
        }
        if (productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
        return this;
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
        for (String name : names)
            kafkaTopics.add(new KafkaTopic("delete", 1, 1, 1, 1800000, name));
        orderServiceSteps.executeAction("kafka_create_topics", this, new JSONObject("{\"topics\": " + CacheService.toJson(kafkaTopics) + "}"));
        for (String name : names)
            Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        topics.addAll(kafkaTopics);
        save();
    }

    public void deleteTopics(List<String> names) {
        orderServiceSteps.executeAction("kafka_delete_topics", this, new JSONObject("{\"topics\": " + CacheService.toJson(names) + "}"));
        for (String name : names)
            Assertions.assertFalse((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        for (String name : names)
            topics.removeIf(topic -> topic.getTopicName().equals(name));
        save();
    }

    public void createAcl(String topicNameRegex) {
        orderServiceSteps.executeAction("kafka_create_acl", this, new JSONObject("{\"client_cn\":\"cnClient\",\"topic_type\":\"all_topics\",\"client_role\":\"consumer\",\"topic_name\":\"" + topicNameRegex + "\"}"));
        save();
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, topicNameRegex)));
    }

    public void createAclTransaction(String transactionRegex) {
        orderServiceSteps.executeAction("kafka_create_transaction_acl", this, new JSONObject("{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\"" + transactionRegex + "\"}"));
        save();
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)));
    }

    /**
     * @param topicNameRegex имя Acl, Если в aclName передать "*" то удалятся все Acl
     */
    public void deleteAcl(String topicNameRegex) {
        orderServiceSteps.executeAction("kafka_delete_acls", this, new JSONObject("{\"acls\":[{\"client_cn\":\"cnClient\",\"topic_type\":\"all_topics\",\"client_role\":\"consumer\",\"topic_name\":\"" + topicNameRegex + "\"}]}}"));
        save();
        Assertions.assertFalse((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, topicNameRegex)));
    }

    /**
     *
     * @param transactionRegex имя Acl транзакции, Если в aclTransactionName передать "*" то удалятся все Acl транзакции
     */
    public void deleteAclTransaction(String transactionRegex) {
        orderServiceSteps.executeAction("kafka_delete_transaction_acls", this, new JSONObject("{\"acls\":[{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\"" + transactionRegex + "\"}]}}"));
        save();
        Assertions.assertFalse((Boolean) orderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)));
    }

    public void start() {
        start("start_kafka");
    }

    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        super.updateCerts("kafka_update_certs");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) orderServiceSteps.getProductsField(this, "attrs.preview_items.data.find{it.config.containsKey('certificate_expiration')}.config.certificate_expiration"));
        dateAfterUpdate = dateFormat.parse((String) orderServiceSteps.getProductsField(this, "data.find{it.config.containsKey('certificate_expiration')}.config.certificate_expiration"));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate),
                String.format("Предыдущая дата: %s обновления сертификата больше либо равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point", "/app", 10);
    }

    public void restart() {
        restart("restart_kafka");
    }

    public void stopSoft() {
        stopSoft("stop_kafka");
    }

    @Override
    @Step("Удаление продукта")
    protected void delete() {
        delete("delete_two_layer");
    }

}
