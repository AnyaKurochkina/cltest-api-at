package tests.orderService;

import com.mifmif.common.regex.Generex;
import core.kafka.CustomKafkaConsumer;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.ApacheKafkaCluster;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.enums.KafkaRoles.CONSUMER;
import static core.enums.KafkaRoles.PRODUCER;

@Epic("Продукты")
@Feature("ApacheKafkaCluster Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("apachekafkacluster_astra"), @Tag("prod")})
@Log4j2
public class ApacheKafkaAstraClusterTest extends Tests {

    final String productName = "Apache Kafka Cluster Astra";

    @TmsLink("847102")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ApacheKafkaCluster product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("847103")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить кластерный сертификат {0}")
    void updateCerts(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.updateCerts();
        }
    }


    @TmsLinks({@TmsLink("847099"),@TmsLink("847105")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Пакетное создание/удаление Topic-ов Kafka {0}")
    void createTopic(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            List<String> topics = Stream.generate(new Generex("[a-zA-Z0-9][a-zA-Z0-9.\\-_]*")::random)
                    .limit(new Random().nextInt(20) + 1).distinct().collect(Collectors.toList());
            kafka.createTopics(topics);
            kafka.deleteTopics(topics);
        }
    }

    @TmsLink("847109")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить параметр топиков Kafka Cluster {0}")
    void editTopic(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createTopics(Collections.singletonList("PacketTopicNameForEdit"));
            kafka.editTopics("PacketTopicNameForEdit");
        }
    }

    @TmsLink("847097")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать ACL на топик Kafka {0}")
    void createAcl(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createTopics(Collections.singletonList("PacketTopicNameForAcl"));
            kafka.createAcl("PacketTopicNameForAcl", PRODUCER);
        }
    }

    @TmsLink("847104")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание ACL на транзакцию Kafka {0}")
    void createAclTransaction(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createAclTransaction("*");
        }
    }

    @TmsLink("847108")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания ВМ и брокера Kafka {0}")
    void checkConnection(ApacheKafkaCluster product) {
        product.setProductName(productName);
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

    @TmsLink("847098")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить кластер Kafka {0}")
    void restart(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.restart();
        }
    }

    @TmsLink("883502")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновление дистрибутива ВТБ-Kafka {0}")
    void update(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.upgradeVersion();
        }
    }

    @TmsLink("847106")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Синхронизировать конфигурацию кластера Kafka {0}")
    void syncInfo(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.syncInfo();
        }
    }

    @TmsLinks({@TmsLink("864077"),@TmsLink("864076")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Идемпотентные ACL. Создание/удаление {0}")
    void createIdempotentAcl(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.createIdempotentAcl("cn001");
            kafka.deleteIdempotentAcl("cn001");
        }
    }

    @TmsLink("847107")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Прислать конфигурацию кластера Kafka {0}")
    void sendConfig(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.sendConfig();
        }
    }

    @TmsLinks({@TmsLink("847100"),@TmsLink("847101")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить/выключить кластер Kafka {0}")
    void start(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.stopSoft();
            kafka.start();
        }
    }

    @TmsLink("847095")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.expandMountPoint();
        }
    }

    @TmsLink("1095609")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Увеличить дисковое пространство {0}")
    void kafkaExpandMountPoint(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.kafkaExpandMountPoint();
        }
    }

    @TmsLink("1055546")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить имя кластера {0}")
    void changeName(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.changeName("new-name");
        }
    }

    @TmsLink("1095198")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновление ядра Kafka до версии 2.8.1 {0}")
    void upgrade281(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.upgrade281();
        }
    }

    @TmsLink("1095239")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Вертикальное масштабирование {0}")
    void resize(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.resize();
        }
    }

    @TmsLink("847096")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ApacheKafkaCluster product) {
        product.setProductName(productName);
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.deleteObject();
        }
    }
}
