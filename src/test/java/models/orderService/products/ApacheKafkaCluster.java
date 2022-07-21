package models.orderService.products;

import core.enums.KafkaRoles;
import core.helper.JsonHelper;
import core.kafka.CustomKafkaConsumer;
import core.kafka.CustomKafkaProducer;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
import models.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static core.utils.Waiting.sleep;

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
    String kafkaVersion;
    String domain;
    @Builder.Default
    public List<KafkaTopic> topics = new ArrayList<>();
    Flavor flavor;
    @ToString.Include
    String osVersion;
    public static final String KAFKA_CREATE_TOPICS = "kafka_create_topics";
    public static final String KAFKA_CLUSTER_RETENTION_MS = "data.find{it.type=='cluster'}.data.config.topics.any{it.topic_name=='%s' && it.retention_ms=='%s'}";

    public static final String KAFKA_CLUSTER_ACL_IDEMPOTENT = "data.find{it.type=='cluster'}.data.config.idempotent_acls.any{it.client_cn=='%s'}";

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/apache_kafka_cluster.json";
        if (productName == null) {
            productName = "Apache Kafka Cluster RHEL";
        }
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (kafkaVersion == null)
            kafkaVersion = getRandomProductVersionByPathEnum("kafka_version.enum");
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    private String getIdGeoDistribution() {
        return Objects.requireNonNull(ReferencesStep.getJsonPathList("tags__contains=" + envType().toUpperCase() + ",kafka&directory__name=geo_distribution")
                .getString("find{it.name == 'kafka:zookeeper'}.id"), "Id geo_distribution not found");
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.kafka_version", kafkaVersion)
                .set("$.order.attrs.layout", getIdGeoDistribution())
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())

                .remove("$.order.attrs.ad_logon_grants", isTest())
                //Fix

                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.label", getLabel())
                .build();
    }

    @SneakyThrows
    public void produceMessage(String topicName, String message) {
        String bootstrapServerUrl = (String) OrderServiceSteps.getProductsField(this, CONNECTION_URL);
        CustomKafkaProducer customKafkaProducer = new CustomKafkaProducer(message, bootstrapServerUrl, topicName);
        try {
            customKafkaProducer.doProduce();
        } catch (Exception e) {
            connectVmException("Ошибка подключения к " + getProductName() + " " + e);
        }
        sleep(10000);
    }

    public CustomKafkaConsumer consumeMessage(String topicName) {
        String bootstrapServerUrl = (String) OrderServiceSteps.getProductsField(this, CONNECTION_URL);
        return new CustomKafkaConsumer(topicName, bootstrapServerUrl, true);
    }

    public void createTopics(List<String> names) {
        List<KafkaTopic> kafkaTopics = new ArrayList<>();
        for (String name : names)
            kafkaTopics.add(new KafkaTopic("delete", 1, 1800000, name));
        OrderServiceSteps.executeAction(KAFKA_CREATE_TOPICS, this, new JSONObject("{\"topics\": " + JsonHelper.toJson(kafkaTopics) + "}"), this.projectId);
        for (String name : names)
            Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_TOPIC, name)), "Отсутствует в списке топик " + name);
        topics.addAll(kafkaTopics);
        save();
    }

    public void editTopics(String topic) {
        JSONObject body = new JSONObject("{\n" +
                "  \"changes\": [\n" +
                "    {\n" +
                "      \"operation\": \"change_cleanup_policy\",\n" +
                "      \"parameters\": {\n" +
                "        \"cleanup^policy\": \"delete\",\n" +
                "        \"retention^ms\": 1800001\n" +
                "      },\n" +
                "      \"topic_name\": \"" + topic + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}"
        );
        OrderServiceSteps.executeAction("kafka_edit_topics_release", this, body, this.projectId);
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(KAFKA_CLUSTER_RETENTION_MS, topic, "1800001")), "ACL на топик не был изменен");
    }

    public void deleteTopics(List<String> names) {
        OrderServiceSteps.executeAction("kafka_delete_topics", this, new JSONObject("{\"topics\": " + JsonHelper.toJson(names) + "}"), this.projectId);
        for (String name : names)
            Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        for (String name : names)
            topics.removeIf(topic -> topic.getTopicName().equals(name));
        save();
    }

    public void changeName(String name) {
        OrderServiceSteps.executeAction("kafka_edit_cluster_name", this, new JSONObject().append("new_name", name), this.projectId);
    }

    public void createAcl(String topicName, KafkaRoles role) {
        OrderServiceSteps.executeAction("kafka_create_acl", this, new JSONObject("{\"acls\":[{\"client_cn\":\"APD09.26-1418-kafka-dl-client-cert\",\"topic_type\":\"by_name\",\"client_role\":\"" + role.getRole() + "\",\"topic_names\":[\"" + topicName + "\"]}]}"), this.projectId);
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, role.getRole(), topicName)), "ACL на топик не создался");
        save();
    }

    public void createIdempotentAcl(String clientCn) {
        OrderServiceSteps.executeAction("kafka_create_idempotent_acls_release", this, new JSONObject("{\"acls\":[{\"client_cn\":\"" + clientCn + "\"}]}"), this.projectId);
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_IDEMPOTENT, clientCn)), "CN сертификата клиента не найден");
    }

    public void deleteIdempotentAcl(String clientCn) {
        OrderServiceSteps.executeAction("kafka_delete_idempotent_acls_release", this, new JSONObject("{\"acls\":[{\"client_cn\":\"" + clientCn + "\"}]}"), this.projectId);
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_IDEMPOTENT, clientCn)), "CN сертификата клиента не найден");
    }

    public void createAclTransaction(String transactionRegex) {
        OrderServiceSteps.executeAction("kafka_create_transaction_acl", this, new JSONObject("{\"acls\":[{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\"" + transactionRegex + "\"}]}"), this.projectId);
        save();
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)), "ACL транзакция не создалась");
    }

    /**
     * @param topicName имя Acl, Если в aclName передать "*" то удалятся все Acl
     */
    public void deleteAcl(String topicName, KafkaRoles role) {
        OrderServiceSteps.executeAction("kafka_delete_acls", this, new JSONObject("{\"acls\":[{\"client_cn\":\"APD09.26-1418-kafka-dl-client-cert\",\"topic_type\":\"by_name\",\"client_role\":\"" + role.getRole() + "\",\"topic_names\":[\"" + topicName + "\"]}]}}"), this.projectId);
        save();
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, role.getRole(), topicName)), "ACL на топик не удалился");
    }

    /**
     * @param transactionRegex имя Acl транзакции, Если в aclTransactionName передать "*" то удалятся все Acl транзакции
     */
    public void deleteAclTransaction(String transactionRegex) {
        OrderServiceSteps.executeAction("kafka_delete_transaction_acls", this, new JSONObject("{\"acls\":[{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\"" + transactionRegex + "\"}]}}"), this.projectId);
        save();
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)), "ACL транзакции не удалились");
    }

    public void upgradeVersion() {
        OrderServiceSteps.executeAction("kafka_release_upgrade_version", this, new JSONObject("{dumb: \"empty\"}"), this.projectId);
    }

    public void start() {
        start("start_kafka");
    }

    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate_expiration')}.data.config.certificate_expiration"));
        super.updateCerts("kafka_update_certs");
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('certificate_expiration')}.data.config.certificate_expiration"));
//        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата больше либо равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
        Assertions.assertNotEquals(0, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    public void restart() {
        restart("restart_kafka");
    }

    public void syncInfo() {
        OrderServiceSteps.executeAction("kafka_sync_info", this, null, getProjectId());
    }

    public void sendConfig() {
        OrderServiceSteps.executeAction("kafka_send_config", this, null);
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
