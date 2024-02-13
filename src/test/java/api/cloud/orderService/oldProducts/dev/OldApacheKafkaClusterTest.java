package api.cloud.orderService.oldProducts.dev;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.ApacheKafkaCluster;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;

import static core.enums.KafkaRoles.PRODUCER;
import static models.cloud.orderService.interfaces.ProductStatus.STARTED;
import static models.cloud.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты DEV")
@Feature("ApacheKafkaCluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_apachekafkacluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
@Log4j2
public class OldApacheKafkaClusterTest extends Tests {

    final ApacheKafkaCluster kafka = ApacheKafkaCluster.builder()
            .projectId("proj-1oob0zjo5h")
            .productId("0c36e61d-687c-4a23-914d-7421779301e4")
            .orderId("8a226df0-554e-4bcd-98a7-c72546fe88e8")
            .build();

//    @Order(1)
//    @TmsLink("841715")
//    @DisplayName("Расширить Apache Kafka Cluster OLD")
//    @Test
//    void expandMountPoint() {
//        if (kafka.productStatusIs(STOPPED)) {
//            kafka.start();
//        }
//        kafka.expandMountPoint();
//    }

    @Order(2)
    @TmsLink("841723")
    @DisplayName("Обновить сертификаты Apache Kafka Cluster OLD")
    @Test
    void updateCerts() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.updateCerts();
    }

    @Order(3)
    @TmsLink("841719")
    @DisplayName("Пакетное создание топиков Apache Kafka Cluster OLD")
    @Test
    void createTopic() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createTopics(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"));
        kafka.deleteTopics(Arrays.asList("PacketTopicName1", "PacketTopicName2", "PacketTopicName3"));
    }

    @Order(4)
    @TmsLink("841725")
    @DisplayName("Пакетное удаление топиков Apache Kafka Cluster OLD")
    @Test
    void deleteTopic() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createTopics(Arrays.asList("PacketTopicName01", "PacketTopicName02", "PacketTopicName03"));
        kafka.deleteTopics(Arrays.asList("PacketTopicName01", "PacketTopicName02", "PacketTopicName03"));
    }

//    @Order(5)
//    @TmsLink("841728")
//    @DisplayName("Проверить создание ВМ Kafka")
//    @Test
//    void consume() {
//        if (kafka.productStatusIs(STOPPED)) {
//            kafka.start();
//        }
//        String message = "This message from autotest";
//        String topicName = "PacketTopicNameForAcl5";
//        try {
//            kafka.createTopics(Collections.singletonList(topicName));
//            kafka.createAcl(topicName, PRODUCER);
//            kafka.createAcl(topicName, CONSUMER);
//            CustomKafkaConsumer consumer = kafka.consumeMessage(topicName);
//            kafka.produceMessage(topicName, message);
//            List<ConsumerRecord<String, String>> consumerRecords = consumer.getConsumerRecordList();
//            log.info(String.format("Сообщения из топика %s : %s", topicName, consumerRecords));
//            Assertions.assertTrue(
//                    consumerRecords.stream().anyMatch(record -> record.value().equals(message)),
//                    "Сообщения в топике отсутствуют");
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail(e.getMessage() + " ," + e.getCause());
//        } finally {
//            kafka.deleteAcl(topicName, CONSUMER);
//            kafka.deleteAcl(topicName, PRODUCER);
//            kafka.deleteTopics(Collections.singletonList(topicName));
//        }
//    }

    @Order(6)
    @TmsLink("841717")
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
    @TmsLink("")
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
    @TmsLink("")
    @DisplayName("Удалить ACL на транзакцию Apache Kafka Cluster OLD")
    @Test
    void deleteAclTransaction() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createAclTransaction("test");

        kafka.deleteAclTransaction("test");
    }

    @Order(9)
    @TmsLink("841724")
    @DisplayName("Создать ACL на транзакцию Apache Kafka Cluster OLD")
    @Test
    void createAclTransaction() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createAclTransaction("test");

        kafka.deleteAclTransaction("test");
    }

    @Disabled
    @Order(10)
    @TmsLink("841720")
    @DisplayName("Включить Apache Kafka Cluster OLD")
    @Test
    void start() {
        if (kafka.productStatusIs(STARTED)) {
            kafka.stopSoft();
        }
        kafka.start();
    }

    @Order(11)
    @TmsLink("841726")
    @DisplayName("Синхронизировать syncInfo конфигурацию Apache Kafka Cluster OLD")
    @Test
    void sincInfo() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.syncInfo();
    }

    @Order(12)
    @TmsLink("841727")
    @DisplayName("Прислать конфигурацию Apache Kafka Cluster OLD")
    @Test
    void sendConfig() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.sendConfig();
    }

    @Disabled
    @Order(13)
    @TmsLink("841718")
    @DisplayName("Перезагрузить Apache Kafka Cluster OLD")
    @Test
    void restart() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.restart();
    }

    @Order(14)
    @TmsLink("841729")
    @DisplayName("Изменить параметр топиков Apache Kafka Cluster OLD")
    @Test
    void editTopic() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.createTopics(Collections.singletonList("PacketTopicNameForEdit"));
        kafka.editTopics("PacketTopicNameForEdit");
        kafka.deleteTopics(Collections.singletonList("PacketTopicNameForEdit"));
    }

//    @Order(15)
//    @TmsLink("883533")
//    @Test
//    @DisplayName("Обновление инсталяции Kafka Cluster OLD")
//    void update() {
//        if (kafka.productStatusIs(STOPPED)) {
//            kafka.start();
//        }
//        kafka.upgradeVersion();
//    }

    @Disabled
    @Order(16)
    @TmsLink("841721")
    @DisplayName("Выключить Apache Kafka Cluster OLD")
    @Test
    void stopSoft() {
        if (kafka.productStatusIs(STOPPED)) {
            kafka.start();
        }
        kafka.stopSoft();
    }
}
