package models.cloud.orderService.products;

import core.enums.KafkaRoles;
import core.enums.Role;
import core.helper.JsonHelper;
import core.kafka.CustomKafkaConsumer;
import core.kafka.CustomKafkaProducer;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import models.cloud.subModels.KafkaTopic;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static core.utils.Waiting.sleep;

@Log4j2
@Data
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ApacheKafkaCluster extends IProduct {
    String kafkaVersion;
    @Builder.Default
    public List<KafkaTopic> topics = new ArrayList<>();
    Flavor flavor;
    @ToString.Include
    String osVersion;
    public static final String KAFKA_VERSION_LATEST = "2.13-2.8.2";
    public static final String KAFKA_CREATE_TOPICS = "kafka_create_topics";
    public static final String KAFKA_CLUSTER_RETENTION_MS = "data.find{it.type=='cluster'}.data.config.topics.any{it.topic_name=='%s' && it.retention_ms=='%s'}";
    public static final String KAFKA_CLUSTER_ACL_IDEMPOTENT = "data.find{it.type=='cluster'}.data.config.idempotent_acls.any{it.client_cn=='%s'}";
    public static final String KAFKA_CLUSTER_QUOTAS = "data.find{it.type=='cluster'}.data.config.quotas.any{it.client_cn=='<default>' && it.producer_byte_rate==%d}";

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/apache_kafka_cluster.json";
        if (productName == null) {
            productName = "Apache Kafka Cluster Astra";
        }
        initProduct();
        if (kafkaVersion == null)
//            kafkaVersion = getRandomProductVersionByPathEnum("kafka_version.enum");
            kafkaVersion = "2.13-2.4.1";
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        if (flavor == null)
            flavor = getMinFlavor();
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.kafka_version", kafkaVersion)
                .set("$.order.attrs.layout", getIdGeoDistribution("kafka-4:zookeeper-3"))
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup())
                .set("$.order.attrs.cluster_name", "at-" + new Random().nextInt())
                .remove("$.order.attrs.ad_logon_grants", !isDev())
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
        OrderServiceSteps.runAction(ActionParameters.builder().name(KAFKA_CREATE_TOPICS).product(this)
                .data(new JSONObject("{\"topics\": " + JsonHelper.toJson(kafkaTopics) + "}")).build());
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
                "        \"partitions_number\": 1,\n" +
                "        \"cleanup^policy\": \"delete\",\n" +
                "        \"retention^ms\": 1800001,\n" +
                "      \"_cleanup^limit_by\": \"time\"\n" +
                "      },\n" +
                "      \"topic_names\": [\"" + topic + "\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}"
        );
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_edit_topics_release").product(this).data(body).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this,
                String.format(KAFKA_CLUSTER_RETENTION_MS, topic, "1800001")), "ACL на топик не был изменен");
    }

    public void deleteTopics(List<String> names) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_delete_topics").product(this)
                .data(new JSONObject("{\"topics\": " + JsonHelper.toJson(names) + "}")).build());
        for (String name : names)
            Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_TOPIC, name)));
        for (String name : names)
            topics.removeIf(topic -> topic.getTopicName().equals(name));
        save();
    }

    public void changeName(String name) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_edit_cluster_name").product(this)
                .data(new JSONObject().put("new_name", name).put("accept", true)).build());
    }

    public void resize() {
        resize("resize_kafka_cluster_vms");
    }

    @Override
    protected void resize(String action) {
        List<Flavor> list = ReferencesStep.getProductFlavorsLinkedListByFilter(this);
        Assertions.assertTrue(list.size() > 1, "У продукта меньше 2 flavors");
        Flavor flavor = list.get(list.size() - 1);
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).timeout(Duration.ofMinutes(40))
                .data(new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}").put("check_agree", true)).build());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    public void createAcl(String topicName, KafkaRoles role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_create_acls").product(this)
                .data(new JSONObject("{\"acls\":[{\"client_cn\":\"APD09.26-1418-kafka-dl-client-cert\",\"topic_type\":\"by_name\",\"client_role\":\"" + role.getRole() + "\",\"topic_names\":[\"" + topicName + "\"]}]}")).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, role.getRole(), topicName)), "ACL на топик не создался");
        save();
    }

    public void createIdempotentAcl(String clientCn) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_create_idempotent_acls_release").product(this)
                .data(new JSONObject("{\"acls\":[{\"client_cn\":\"" + clientCn + "\"}]}")).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_IDEMPOTENT, clientCn)), "CN сертификата клиента не найден");
    }

    public void deleteIdempotentAcl(String clientCn) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_delete_idempotent_acls_release").product(this)
                .data(new JSONObject("{\"acls\":[{\"client_cn\":\"" + clientCn + "\"}]}")).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_IDEMPOTENT, clientCn)), "CN сертификата клиента не найден");
    }

    public void createAclTransaction(String transactionRegex) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_create_transaction_acls").product(this)
                .data(new JSONObject("{\"acls\":[{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"by_mask\",\"transaction_id\":\"" + transactionRegex + "\"}]}")).build());
        save();
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)), "ACL транзакция не создалась");
    }

    /**
     * @param topicName имя Acl, Если в aclName передать "*" то удалятся все Acl
     */
    public void deleteAcl(String topicName, KafkaRoles role) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_delete_acls").product(this)
                .data(new JSONObject("{\"acls\":[{\"client_cn\":\"APD09.26-1418-kafka-dl-client-cert\",\"topic_type\":\"by_name\",\"client_role\":\"" + role.getRole() + "\",\"topic_names\":[\"" + topicName + "\"]}]}}")).build());
        save();
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TOPICS, role.getRole(), topicName)), "ACL на топик не удалился");
    }

    /**
     * @param transactionRegex имя Acl транзакции, Если в aclTransactionName передать "*" то удалятся все Acl транзакции
     */
    public void deleteAclTransaction(String transactionRegex) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_delete_transaction_acls").product(this)
                .data(new JSONObject("{\"acls\":[{\"client_cn\":\"cnClient\",\"transaction_id_type\":\"all_ids\",\"transaction_id\":\"" + transactionRegex + "\"}]}}")).build());
        save();
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_ACL_TRANSACTIONS, transactionRegex)), "ACL транзакции не удалились");
    }

    public void upgradeVersion() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_release_upgrade_version").product(this)
                .data(new JSONObject().put("accept", true)).build());
    }

    public void addDefaultQuota(int quota) {
        if(!kafkaVersion.equals(KAFKA_VERSION_LATEST))
            upgrade281();
        JSONObject json = JsonHelper.getJsonTemplate("/orders/kafka_quota.json")
                .set("$.quotas[0].producer_byte_rate", quota)
                .build();
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_create_quotas").product(this).data(json).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_QUOTAS, quota)));
    }

    public void deleteDefaultQuota(int quota) {
        JSONObject json = JsonHelper.getJsonTemplate("/orders/kafka_quota.json")
                .remove("$.quotas[0].producer_byte_rate")
                .build();
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_delete_quotas").product(this).data(json).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(KAFKA_CLUSTER_QUOTAS, quota)));
    }

    public void upgrade281() {
        if(!kafkaVersion.equals(KAFKA_VERSION_LATEST)) {
            OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_upgrade_28x").product(this)
                    .data(new JSONObject().put("dumb", "empty").put("accept", true)).build());
            Assertions.assertEquals(KAFKA_VERSION_LATEST, OrderServiceSteps.getProductsField(this, "data.find{it.type=='cluster'}.data.config.kafka_version"), "Версия kafka не изменилась");
            kafkaVersion = KAFKA_VERSION_LATEST;
            save();
        }
    }

    public void start() {
        start("start_kafka");
    }

    @SneakyThrows
    @Override
    protected void updateCerts(String action) {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        OrderServiceSteps.runAction(ActionParameters.builder().name(action).product(this).data(new JSONObject().put("accept", true)).build());
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        Assertions.assertNotEquals(0, dateBeforeUpdate.compareTo(dateAfterUpdate), String.format("Предыдущая дата: %s обновления сертификата равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    public void updateCerts() {
        updateCerts("kafka_update_certs");
    }

    public void updateCertsInterrupting() {
        updateCerts("kafka_update_certs_interrupting");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    public void kafkaExpandMountPoint() {
        int size = 10;
        String mount = "/app";
        Float sizeBefore = (Float) OrderServiceSteps.getProductsField(this, String.format(EXPAND_MOUNT_SIZE, mount, mount));
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_expand_mount_point").product(this)
                .data(new JSONObject().put("size", size).put("mount", mount).put("accept", true)).build());
        float sizeAfter = (Float) OrderServiceSteps.getProductsField(this, String.format(CHECK_EXPAND_MOUNT_SIZE, mount, mount, sizeBefore.intValue()));
        Assertions.assertEquals(sizeBefore, sizeAfter - size, 0.05, "sizeBefore >= sizeAfter");
    }

    public void restart() {
        restart("restart_kafka");
    }

    public void syncInfo() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_sync_info").product(this).build());
    }

    public void sendConfig() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("kafka_send_config").product(this).role(Role.ORDER_SERVICE_ADMIN).build());
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
