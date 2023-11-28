package api.cloud.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Rhel;
import models.cloud.portalBack.AccessGroup;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;

@Deprecated
@Epic("Продукты")
@Feature("Rhel")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("rhel"), @Tag("prod")})
@Disabled
public class RhelTest extends Tests {

    @TmsLink("377711")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Rhel product, Integer num) {
        //noinspection EmptyTryBlock
        try (Rhel rhel = product.createObjectExclusiveAccess()){}
    }

    @TmsLink("377705")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(Rhel product, Integer num) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.expandMountPoint();
        }
    }

    @TmsLink("377707")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(Rhel product, Integer num) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.restart();
        }
    }

    @TmsLink("377710")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(Rhel product, Integer num) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.stopSoft();
            rhel.start();
        }
    }

    @TmsLink("377712")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(Rhel product, Integer num) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.stopHard();
            try {
                rhel.resize(rhel.getMaxFlavorLinuxVm());
            } finally {
                rhel.start();
            }
        }
    }

    @TmsLinks({@TmsLink("377708"),@TmsLink("377709")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(Rhel product, Integer num) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.stopHard();
            rhel.start();
        }
    }

    @TmsLink("377706")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(Rhel product, Integer num) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.deleteObject();
        }
    }
}
