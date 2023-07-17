package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Podman;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("Podman Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("podman_astra"), @Tag("prod")})
@DisabledIfEnv("ift")
public class PodmanAstraTest extends Tests {

    @TmsLink("820506")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Podman product) {
        //noinspection EmptyTryBlock
        try (Podman podman = product.createObjectExclusiveAccess()) {
        }
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

    @Disabled
    @TmsLink("820504")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.restart();
        }
    }

    @Disabled
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

    @Disabled
    @TmsLinks({@TmsLink("820505"), @TmsLink("820503")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.stopHard();
            podman.start();
        }
    }

    @TmsLink("851394")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Проверка создания {0}")
    void checkCreate(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.checkCertsBySsh();
            assertContains(podman.executeSsh("podman ps -a"),
                    "CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES");
        }
    }

    @TmsLink("1091842")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка прав у ролей пользователя {0}")
    void checkUserGroup(Podman product) {
        try (Podman podman = product.createObjectExclusiveAccess()) {
            podman.checkUserGroupBySsh();
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
