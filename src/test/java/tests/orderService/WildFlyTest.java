package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.WildFly;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import ru.testit.annotations.WorkItemId;
import tests.Tests;

@Epic("Продукты")
@Feature("WildFly")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("wildfly"), @Tag("prod")})
public class WildFlyTest extends Tests {

    @WorkItemId("377474")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Заказ {0}")
    void create(WildFly product) {
        //noinspection EmptyTryBlock
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {}
    }

    @WorkItemId("377467")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.expandMountPoint();
        }
    }

    @WorkItemId("377470")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.restart();
        }
    }

    @WorkItemId("377473")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopSoft();
            wildFly.start();
        }
    }

    @WorkItemId("377469")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard();
            try {
                wildFly.resize();
            } finally {
                wildFly.start();
            }
        }
    }

    @WorkItemId("377472")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @WorkItemId("377471")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @WorkItemId("377477")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.updateCerts();
        }
    }

    @WorkItemId("377468")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.deleteObject();
        }
    }
}
