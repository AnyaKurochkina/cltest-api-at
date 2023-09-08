package api.cloud.orderService;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.kafka.CustomKafkaConsumer;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.products.ApacheKafkaCluster;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.DisabledIfEnv;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.enums.KafkaRoles.CONSUMER;
import static core.enums.KafkaRoles.PRODUCER;

@Epic("Продукты")
@Feature("ApacheKafkaCluster Astra")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({@Tag("regress"), @Tag("orders"), @Tag("apachekafkacluster_astra"), @Tag("prod")})
@DisabledIfEnv("ift")
@Log4j2
public class ApacheKafkaAstraClusterTest extends Tests {

    @TmsLink("847102")
    @Order(1)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ApacheKafkaCluster product) {
        //noinspection EmptyTryBlock
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("847103")
    @Order(2)
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить кластерный сертификат {0}")
    void updateCerts(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.updateCerts();
        }
    }

    @TmsLink("1348294")
    @Order(3)
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить кластерный сертификат (аварийно) {0}")
    void updateCertsInterrupting(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.updateCertsInterrupting();
        }
    }

    @TmsLinks({@TmsLink("847099"), @TmsLink("847105")})
    @Tag("actions")
    @Order(4)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Пакетное создание/удаление Topic-ов Kafka {0}")
    void createTopic(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            List<String> topics = Stream.generate(new Generex("[a-zA-Z0-9][a-zA-Z0-9.\\-_]*")::random)
                    .limit(new Random().nextInt(20) + 1).distinct().collect(Collectors.toList());
            kafka.createTopics(topics);
            kafka.deleteTopics(topics);
        }
    }

    @TmsLink("847109")
    @Tag("actions")
    @Order(5)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить параметр топиков Kafka Cluster {0}")
    void editTopic(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createTopics(Collections.singletonList("PacketTopicNameForEdit"));
            kafka.editTopics("PacketTopicNameForEdit");
        }
    }

    @TmsLink("847097")
    @Tag("actions")
    @Order(6)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать ACL на топик Kafka {0}")
    void createAcl(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createTopics(Collections.singletonList("PacketTopicNameForAcl"));
            kafka.createAcl("PacketTopicNameForAcl", PRODUCER);
        }
    }

    @TmsLink("847104")
    @Tag("actions")
    @Order(7)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание ACL на транзакцию Kafka {0}")
    void createAclTransaction(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createAclTransaction("*");
        }
    }

    @TmsLink("847108")
    @Tag("actions")
    @Order(8)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания ВМ и брокера Kafka {0}")
    void checkConnection(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            String topicName = "PacketTopicNameForAcl5";
            String message = "This message from autotest";
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
        }
    }

    @Disabled
    @TmsLink("847098")
    @Tag("actions")
    @Order(9)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить кластер Kafka {0}")
    void restart(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.restart();
        }
    }

    @TmsLink("883502")
    @Tag("actions")
    @Order(10)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновление дистрибутива ВТБ-Kafka {0}")
    void update(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.upgradeVersion();
        }
    }

    @TmsLink("847106")
    @Tag("actions")
    @Order(11)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Синхронизировать конфигурацию кластера Kafka {0}")
    void syncInfo(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.syncInfo();
        }
    }

    @TmsLinks({@TmsLink("864077"), @TmsLink("864076")})
    @Tag("actions")
    @Order(12)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Идемпотентные ACL. Создание/удаление {0}")
    void createIdempotentAcl(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createIdempotentAcl("cn001");
            kafka.deleteIdempotentAcl("cn001");
        }
    }

    @TmsLink("847107")
    @Tag("actions")
    @Order(13)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Прислать конфигурацию кластера Kafka {0}")
    void sendConfig(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.sendConfig();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("847100"), @TmsLink("847101")})
    @Tag("actions")
    @Order(14)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить/выключить кластер Kafka {0}")
    void start(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.stopSoft();
            kafka.start();
        }
    }

    @Disabled
    @TmsLink("847095")
    @Tag("actions")
    @Order(15)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.expandMountPoint();
        }
    }

    @TmsLink("1095609")
    @Tag("actions")
    @Order(16)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Увеличить дисковое пространство {0}")
    void kafkaExpandMountPoint(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.kafkaExpandMountPoint();
        }
    }

    @TmsLink("1055546")
    @Tag("actions")
    @Order(17)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить имя кластера {0}")
    void changeName(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.changeName("new-name");
        }
    }

    @TmsLink("1095198")
    @Tag("actions")
    @Order(18)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновление ядра Kafka до версии 2.8.1 {0}")
    void upgrade281(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.upgrade281();
        }
    }

    @TmsLinks({@TmsLink("1652046"), @TmsLink("1652048")})
    @Tag("actions")
    @Order(19)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Пакетное создание/удаление квот Kafka {0}")
    void addAndRemoveQuota(ApacheKafkaCluster product) {
        int quota = 131072;
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.addDefaultQuota(quota);
            kafka.deleteDefaultQuota(quota);
        }
    }

    @TmsLink("1095239")
    @Order(20)
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Вертикальное масштабирование {0}")
    void resize(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.resize();
        }
    }

    @TmsLink("847096")
    @Order(100)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    void delete(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.deleteObject();
        }
    }
}
