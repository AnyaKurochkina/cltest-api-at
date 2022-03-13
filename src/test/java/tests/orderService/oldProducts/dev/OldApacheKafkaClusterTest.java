package tests.orderService.oldProducts.dev;

import core.kafka.CustomKafkaConsumer;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.ApacheKafkaCluster;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.enums.KafkaRoles.CONSUMER;
import static core.enums.KafkaRoles.PRODUCER;
import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;
import static org.junit.jupiter.api.Assertions.fail;

@Epic("Старые продукты DEV")
@Feature("ApacheKafkaCluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_apachekafkacluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Log4j2
public class OldApacheKafkaClusterTest extends Tests {

    final ApacheKafkaCluster kafka = ApacheKafkaCluster.builder()
            .projectId("proj-67nljbzjtt")
            .productId("d46dd919-defc-4ec6-a55b-2017b3981258")
            .orderId("62758afa-911e-4ee8-abd2-a59892e3426f")
            .productName("Apache Kafka Cluster")
            .build();

    @Order(1)
    @DisplayName("Расширить Apache Kafka Cluster OLD")
    @Test
    void expandMountPoint() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Обновить сертификаты Apache Kafka Cluster OLD")
    @Test
    void updateCerts() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.updateCerts();
    }

    @Order(3)
    @DisplayName("Создать топик Apache Kafka Cluster OLD")
    @Test
    void createTopic() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createTopics(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"));
        kafka.deleteTopics(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"));
    }

    @Order(4)
    @DisplayName("Удалить топик Apache Kafka Cluster OLD")
    @Test
    void deleteTopic() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createTopics(Arrays.asList("PacketTopicName01", "PacketTopicName02", "PacketTopicName03"));
        kafka.deleteTopics(Arrays.asList("PacketTopicName01", "PacketTopicName02", "PacketTopicName03"));
    }

    @Order(5)
    @DisplayName("Удалить топик Apache Kafka Cluster OLD")
    @Test
    void consume() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        String message = "This message from autotest";
        String topicName = "PacketTopicNameForAcl5";
        try {
            kafka.createTopics(Collections.singletonList(topicName));
            kafka.createAcl(topicName, PRODUCER);
            kafka.createAcl(topicName, CONSUMER);
            CustomKafkaConsumer consumer = kafka.consumeMessage(topicName);
            kafka.produceMessage(topicName, message);
            List<ConsumerRecord<String, String>> consumerRecords = consumer.getConsumerRecordList();
            log.info(String.format("Сообщения из топика %s : %s", topicName, consumerRecords));
            Assertions.assertTrue(
                    consumerRecords.stream().anyMatch(record -> record.value().equals(message)),
                    "Сообщения в топике отсутствуют");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage() + " ," + e.getCause());
        } finally {
            kafka.deleteAcl(topicName, CONSUMER);
            kafka.deleteAcl(topicName, PRODUCER);
            kafka.deleteTopics(Collections.singletonList(topicName));
        }
    }

    @Order(6)
    @DisplayName("Создать ACL Apache Kafka Cluster OLD")
    @Test
    void createAcl() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createTopics(Collections.singletonList("PacketTopicNameForAcl"));
        kafka.createAcl("PacketTopicNameForAcl", PRODUCER);

        kafka.deleteAcl("PacketTopicNameForAcl", PRODUCER);
        kafka.deleteTopics(Collections.singletonList("PacketTopicNameForAcl"));
    }

    @Order(7)
    @DisplayName("Удалить ACL Apache Kafka Cluster OLD")
    @Test
    void deleteAcl() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createTopics(Collections.singletonList("PacketTopicNameForAcl1"));
        kafka.createAcl("PacketTopicNameForAcl1", PRODUCER);

        kafka.deleteAcl("PacketTopicNameForAcl1", PRODUCER);
        kafka.deleteTopics(Collections.singletonList("PacketTopicNameForAcl1"));
    }

    @Order(8)
    @DisplayName("Удалить ACL транзакцию Apache Kafka Cluster OLD")
    @Test
    void deleteAclTransaction() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createAclTransaction("*");

        kafka.deleteAclTransaction("*");
    }

    @Order(9)
    @DisplayName("Создать ACL транзакцию Apache Kafka Cluster OLD")
    @Test
    void createAclTransaction() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createAclTransaction("*");

        kafka.deleteAclTransaction("*");
    }

    @Order(10)
    @DisplayName("Включить Apache Kafka Cluster OLD")
    @Test
    void start() {
        if (kafka.productStatusIs(STARTED)) {
            kafka.stopSoft();
        }
        kafka.start();
    }

    @Order(11)
    @DisplayName("Синхронизировать конфигурацию Apache Kafka Cluster OLD")
    @Test
    void sincInfo() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.syncInfo();
    }

    @Order(12)
    @DisplayName("Прислать конфигурацию Apache Kafka Cluster OLD")
    @Test
    void sendConfig() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.sendConfig();
    }

    @Order(13)
    @DisplayName("Выключить Apache Kafka Cluster OLD")
    @Test
    void stopSoft() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.stopSoft();
    }
}
