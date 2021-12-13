package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
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

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(RabbitMQCluster product) {
        //noinspection EmptyTryBlock
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {}
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.stopSoft();
            rabbit.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать пользователя RabbitMQ {0}")
    void createUser(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.rabbitmqCreateUser();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.stopHard();
            rabbit.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(RabbitMQCluster product) {
        try (RabbitMQCluster rabbitMQCluster = product.createObjectExclusiveAccess()) {
            rabbitMQCluster.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbitMQCluster.updateCerts();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rabbit.stopHard();
            rabbit.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.deleteObject();
        }
    }
}
