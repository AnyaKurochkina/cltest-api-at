package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Nginx;
import models.orderService.products.Redis;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import static models.orderService.interfaces.IProduct.*;

@Epic("Продукты")
@Feature("Nginx")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("nginx")})
public class NginxTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Nginx product) {
        Nginx nginx = product.createObjectExclusiveAccess();
        nginx.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.stopSoft();
            nginx.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.stopHard();
            nginx.resize();
            nginx.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.checkPreconditionStatusProduct(ProductStatus.CREATED);
            nginx.stopHard();
            nginx.start();
        }
    }

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
    @Deleted
    void delete(Nginx product) {
        try (Nginx nginx = product.createObjectExclusiveAccess()) {
            nginx.deleteObject();
        }
    }
}
