package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Nginx;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Nginx")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("nginx"), @Tag("prod")})
public class NginxTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Nginx product) {
        //noinspection EmptyTryBlock
        try (Nginx nginx = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.expandMountPoint();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.restart();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.stopSoft();
            nginx.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.stopHard();
            try {
                nginx.resize();
            } finally {
                nginx.start();
            }

        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.stopHard();
            nginx.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.stopHard();
            nginx.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.deleteObject();
        }
    }
}
