package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.RabbitMQCluster;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("RabbitMQCluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("rabbitmqcluster"), @Tag("prod")})
public class RabbitMQClusterTest extends Tests {

    @TmsLink("377645")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(RabbitMQCluster product) {
        //noinspection EmptyTryBlock
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377638")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.expandMountPoint();
        }
    }

    @TmsLink("653492")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.refreshVmConfig();
        }
    }

    @TmsLink("377641")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.restart();
        }
    }

    @TmsLink("377644")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.stopSoft();
            rabbit.start();
        }
    }

    @TmsLink("377656")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать пользователя RabbitMQ {0}")
    void createUser(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.rabbitmqCreateUser();
        }
    }

    @TmsLink("377643")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.stopHard();
            rabbit.start();
        }
    }

    @TmsLink("377646")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(RabbitMQCluster product) {
        try (RabbitMQCluster rabbitMQCluster = product.createObjectExclusiveAccess()) {
            rabbitMQCluster.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbitMQCluster.updateCerts();
        }
    }

    @TmsLink("377642")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.stopHard();
            rabbit.start();
        }
    }

    @TmsLink("377639")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.deleteObject();
        }
    }
}
