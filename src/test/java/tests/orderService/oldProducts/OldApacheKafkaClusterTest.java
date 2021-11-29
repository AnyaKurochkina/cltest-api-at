package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.ApacheKafkaCluster;
import org.junit.jupiter.api.*;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;

@Epic("Старые продукты")
@Feature("ApacheKafkaCluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_apachekafkacluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldApacheKafkaClusterTest extends Tests {

    ApacheKafkaCluster kafka = ApacheKafkaCluster.builder()
            .projectId("proj-67nljbzjtt")
            .productId("d46dd919-defc-4ec6-a55b-2017b3981258")
            .orderId("62758afa-911e-4ee8-abd2-a59892e3426f")
            .productName("Apache Kafka Cluster")
            .build();

    @Order(1)
    @DisplayName("Расширить Apache Kafka Cluster OLD")
    @Test
    void expandMountPoint() {
        try {
            kafka.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            kafka.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Обновить сертификаты Apache Kafka Cluster OLD")
    @Test
    void updateCerts() {
        kafka.updateCerts();
    }

    @Order(3)
    @DisplayName("Создать топик Apache Kafka Cluster OLD")
    @Test
    void createTopic() {
        kafka.createTopics(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"));
        kafka.deleteTopics(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"));
    }

    @Order(4)
    @DisplayName("Удалить топик Apache Kafka Cluster OLD")
    @Test
    void deleteTopic() {
        kafka.createTopics(Arrays.asList("PacketTopicName01", "PacketTopicName02", "PacketTopicName03"));
        kafka.deleteTopics(Arrays.asList("PacketTopicName01", "PacketTopicName02", "PacketTopicName03"));
    }

    @Order(5)
    @DisplayName("Создать ACL Apache Kafka Cluster OLD")
    @Test
    void createAcl() {
        kafka.createTopics(Collections.singletonList("PacketTopicNameForAcl"));
        kafka.createAcl("*");

        kafka.deleteAcl("*");
        kafka.deleteTopics(Collections.singletonList("PacketTopicNameForAcl"));
    }

    @Order(6)
    @DisplayName("Удалить ACL Apache Kafka Cluster OLD")
    @Test
    void deleteAcl() {
        kafka.createTopics(Collections.singletonList("PacketTopicNameForAcl1"));
        kafka.createAcl("*");

        kafka.deleteAcl("*");
        kafka.deleteTopics(Collections.singletonList("PacketTopicNameForAcl1"));
    }

    @Order(7)
    @DisplayName("Удалить ACL транзакцию Apache Kafka Cluster OLD")
    @Test
    void deleteAclTransaction() {
        kafka.createAclTransaction("*");

        kafka.deleteAclTransaction("*");
    }

    @Order(8)
    @DisplayName("Создать ACL транзакцию Apache Kafka Cluster OLD")
    @Test
    void createAclTransaction() {
        kafka.createAclTransaction("*");

        kafka.deleteAclTransaction("*");
    }

    @Order(9)
    @DisplayName("Включить Apache Kafka Cluster OLD")
    @Test
    void start() {
        kafka.stopSoft();
        kafka.start();
    }

    @Order(10)
    @DisplayName("Изменить конфигурацию Apache Kafka Cluster OLD")
    @Test
    void resize() {
        kafka.restart();
    }

    @Order(11)
    @DisplayName("Выключить Apache Kafka Cluster OLD")
    @Test
    void stopSoft() {
        kafka.stopSoft();
    }
}
