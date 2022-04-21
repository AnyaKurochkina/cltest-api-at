package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Podman;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Podman")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("podman_astra"), @Tag("prod")})
public class PodmanAstraTest extends Tests {

    @TmsLink("820506")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Podman product) {
        //noinspection EmptyTryBlock
        try (Podman podman = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("820507")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.expandMountPoint();
        }
    }

    @TmsLink("820504")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.restart();
        }
    }

    @TmsLink("820501")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.stopSoft();
            podman.start();
        }
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

    @TmsLink("820503")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.stopHard();
            podman.start();
        }
    }

    @TmsLink("820505")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.stopHard();
            podman.start();
        }
    }

    @TmsLink("820502")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.deleteObject();
        }
    }
}
