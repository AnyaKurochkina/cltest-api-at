package api.cloud.orderService;

import api.Tests;
import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("astra"), @Tag("prod")})
public class AstraTest extends Tests {

    @TmsLink("391703")
    @Tag("actions")
    @Tag("health_check")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Astra product, Integer num) {
        //noinspection EmptyTryBlock
        try (Astra astra = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("391705")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.expandMountPoint();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить ОС {0}")
    void updateOsVm(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(astra.isDev(), "Тест включен только для dev среды");
            astra.updateOsVm();
        }
    }

    @Disabled
    @TmsLink("391699")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.restart();
        }
    }

    @TmsLink("1685463")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Реинвентаризация ВМ (Linux) {0}")
    void updateVmInfo(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(astra.isDev(), "Тест включен только для dev среды");
            Http.setFixedRole(Role.ORDER_SERVICE_ADMIN);
            try {
                astra.updateVmInfo();
            } finally {
                Http.removeFixedRole();
            }
        }
    }

    @Disabled
    @TmsLink("391702")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.stopSoft();
            astra.start();
        }
    }

    @TmsLink("391704")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.resize(astra.getMaxFlavorLinuxVm());
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.stopHard();
            astra.start();
        }
    }

    @TmsLink("1090927")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка прав у ролей пользователя {0}")
    void checkCreate(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkUserGroupBySsh();
        }
    }

    @TmsLink("382910")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания {0}")
    void checkCreateAd(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkCertsBySsh();
        }
    }

    @TmsLinks({@TmsLink("1733768"), @TmsLink("1733769")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать/удалить снапшот {0}")
    void createSnapshot(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(astra.isDev(), "Тест включен только для dev среды");
            Http.setFixedRole(Role.ORDER_SERVICE_ADMIN);
            try {
                astra.createSnapshot(1);
                astra.deleteSnapshot();
            } finally {
                Http.removeFixedRole();
            }
        }
    }

    @TmsLink("391698")
    @Tag("health_check")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(Astra product, Integer num) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.deleteObject();
        }
    }
}
