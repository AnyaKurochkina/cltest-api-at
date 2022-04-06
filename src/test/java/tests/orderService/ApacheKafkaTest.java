package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.ApacheKafka;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ApacheKafka")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("apachekafka"), @Tag("prod")})
public class ApacheKafkaTest extends Tests {

    @TmsLink("377693")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ApacheKafka product) {
        //noinspection EmptyTryBlock
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377686")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.expandMountPoint();
        }
    }

    @TmsLink("377689")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.restart();
        }
    }

    @TmsLink("377692")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.stopSoft();
            kafka.start();
        }
    }

    @TmsLink("377688")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.stopHard();
            try {
                kafka.resize(kafka.getMaxFlavor());
            } finally {
                kafka.start();
            }
        }
    }

    @TmsLink("377691")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.stopHard();
            kafka.start();
        }
    }

    @TmsLink("377690")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.stopHard();
            kafka.start();
        }
    }

    @TmsLink("377687")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ApacheKafka product) {
        try (ApacheKafka kafka = product.createObjectExclusiveAccess()) {
            kafka.deleteObject();
        }
    }
}
