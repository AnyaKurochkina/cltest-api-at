package tests.orderService.oldProducts.test;

import core.enums.KafkaRoles;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.ApacheKafkaCluster;
import org.junit.jupiter.api.*;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;

import static core.enums.KafkaRoles.PRODUCER;
import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты TEST")
@Feature("ApacheKafkaCluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_apachekafkacluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldApacheKafkaClusterTest extends Tests {

    final ApacheKafkaCluster kafka = ApacheKafkaCluster.builder()
            .projectId("proj-juh8ynkvtn")
            .productId("d46dd919-defc-4ec6-a55b-2017b3981258")
            .orderId("df835386-d7ea-4ad7-8d06-05d64d0b2acb")
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

    @Order(6)
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

    @Order(7)
    @DisplayName("Удалить ACL транзакцию Apache Kafka Cluster OLD")
    @Test
    void deleteAclTransaction() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createAclTransaction("*");

        kafka.deleteAclTransaction("*");
    }

    @Order(8)
    @DisplayName("Создать ACL транзакцию Apache Kafka Cluster OLD")
    @Test
    void createAclTransaction() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createAclTransaction("*");

        kafka.deleteAclTransaction("*");
    }

    @Order(9)
    @DisplayName("Включить Apache Kafka Cluster OLD")
    @Test
    void start() {
        if (kafka.productStatusIs(STARTED)) {
            kafka.stopSoft();
        }
        kafka.start();
    }

    @Order(10)
    @DisplayName("Изменить конфигурацию Apache Kafka Cluster OLD")
    @Test
    void resize() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.restart();
    }

    @Order(11)
    @DisplayName("Выключить Apache Kafka Cluster OLD")
    @Test
    void stopSoft() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.stopSoft();
    }
}
