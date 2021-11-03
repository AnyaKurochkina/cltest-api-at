package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.ApacheKafka;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ApacheKafka")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("apachekafka")})
public class ApacheKafkaTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ApacheKafka product) {
        ApacheKafka kafka = product.createObjectExclusiveAccess();
        kafka.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.stopSoft();
            kafka.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.stopHard();
            kafka.resize();
            kafka.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.stopHard();
            kafka.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.checkPreconditionStatusProduct(ProductStatus.CREATED);
            kafka.stopHard();
            kafka.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.deleteObject();
        }
    }
}
