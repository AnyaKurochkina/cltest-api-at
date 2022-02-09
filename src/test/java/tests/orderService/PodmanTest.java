package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Podman;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Podman")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("podman"), @Tag("prod")})
public class PodmanTest extends Tests {

    @TmsLink("377809")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Podman product) {
        //noinspection EmptyTryBlock
        try (Podman podman = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("653489")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.checkPreconditionStatusProduct(ProductStatus.CREATED);
            podman.expandMountPoint();
        }
    }

    @TmsLink("377805")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.checkPreconditionStatusProduct(ProductStatus.CREATED);
            podman.restart();
        }
    }

    @TmsLink("377808")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.checkPreconditionStatusProduct(ProductStatus.CREATED);
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

    @TmsLink("377807")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.checkPreconditionStatusProduct(ProductStatus.CREATED);
            podman.stopHard();
            podman.start();
        }
    }

    @TmsLink("377806")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.checkPreconditionStatusProduct(ProductStatus.CREATED);
            podman.stopHard();
            podman.start();
        }
    }

    @TmsLink("377804")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.deleteObject();
        }
    }
}
