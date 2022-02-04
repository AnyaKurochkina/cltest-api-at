package tests.orderService;

import com.mifmif.common.regex.Generex;
import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.ApacheKafkaCluster;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epic("Продукты")
@Feature("ApacheKafkaCluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("apachekafkacluster"), @Tag("prod")})
public class ApacheKafkaClusterTest extends Tests {

    @TmsLink("377732")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ApacheKafkaCluster product) {
        //noinspection EmptyTryBlock
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377734")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.updateCerts();
        }
    }

    @TmsLink("377729")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Пакетное создание Topic-ов Kafka {0}")
    void createTopic(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.createTopics(Stream.generate(new Generex("[a-zA-Z0-9][a-zA-Z0-9.\\-_]*")::random)
                    .limit(new Random().nextInt(20) + 1).distinct().collect(Collectors.toList()));
        }
    }

    @TmsLink("377736")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Пакетное удаление Topic-ов Kafka {0}")
    void deleteTopic(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.createTopics(Arrays.asList("PacketTopicName01", "PacketTopicName02", "PacketTopicName03"));
            kafka.deleteTopics(Arrays.asList("PacketTopicName01", "PacketTopicName03"));
        }
    }

    @TmsLink("377727")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать ACL Kafka {0}")
    void createAcl(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.createTopics(Collections.singletonList("PacketTopicNameForAcl"));
            kafka.createAcl("*");
        }
    }

    @TmsLink("377735")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание ACL на транзакцию Kafka {0}")
    void createAclTransaction(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.createAclTransaction("*");
        }
    }

    @TmsLink("377731")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить кластер Kafka {0}")
    void stopSoft(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.stopSoft();
            kafka.start();
        }
    }

    @TmsLink("377728")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить кластер Kafka {0}")
    void resize(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.restart();
        }
    }

    @TmsLink("377730")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить кластер Kafka {0}")
    void start(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.stopSoft();
            kafka.start();
        }
    }

    @TmsLink("377725")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.expandMountPoint();
        }
    }

    @TmsLink("377726")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ApacheKafkaCluster product) {
        try (ApacheKafkaCluster kafka = product.createObjectExclusiveAccess()) {
            kafka.deleteObject();
        }
    }
}
