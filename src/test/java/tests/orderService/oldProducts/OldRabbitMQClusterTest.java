package tests.orderService.oldProducts;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.RabbitMQCluster;
import models.orderService.products.Rhel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Старые продукты")
@Feature("RabbitMQCluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_rabbitmqcluster"), @Tag("prod"),  @Tag("old")})
public class OldRabbitMQClusterTest extends Tests {
//не готов
    RabbitMQCluster rabbitMQCluster = RabbitMQCluster.builder()
            .projectId("proj-67nljbzjtt")
            .productId("9c121d8f-3d9d-49e3-9e64-9d5c0c067e53")
            .orderId("c5e4c8e4-55b4-4cf7-b975-49f7eb3cae9a")//c5e4c8e4-55b4-4cf7-b975-49f7eb3cae9a создал новый(старый бажный)
            .productName("RabbitMQCluster")
            .build();

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
    void resize(RabbitMQCluster product) {
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
    @Deleted
    void delete(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.deleteObject();
        }
    }
}
