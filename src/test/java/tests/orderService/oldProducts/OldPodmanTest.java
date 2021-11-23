package tests.orderService.oldProducts;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Podman;
import models.orderService.products.PostgreSQL;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Podman")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("podman"), @Tag("prod")})
public class OldPodmanTest extends Tests {

    Podman podman = Podman.builder()
            .projectId("proj-67nljbzjtt")
            .productId("3b3807a6-9ad0-4ca6-930a-a37efffcc605")
            .orderId("f6dd249c-b124-40b0-a99a-a40e55d5b5ce")
            .build();

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Podman product) {
            podman.expandMountPoint();
    }

//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Перезагрузить {0}")
//    void restart(Podman product) {
//        try (Podman podman = product.createObjectExclusiveAccess()) {
//            podman.checkPreconditionStatusProduct(ProductStatus.CREATED);
//            podman.restart();
//        }
//    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Podman product) {
            podman.stopSoft();
            podman.start();
    }

//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Изменить конфигурацию {0}")
//    void resize(Podman product) {
//        try (Podman podman = product.createObjectExclusiveAccess()) {
//            podman.checkPreconditionStatusProduct(ProductStatus.CREATED);
//            podman.stopHard();
//            try {
//                podman.resize();
//            } finally {
//                podman.start();
//            }
//        }
//    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Podman product) {
            podman.stopHard();
            podman.start();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Podman product) {
            podman.stopHard();
            podman.start();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(Podman product) {
            podman.deleteObject();
    }
}
