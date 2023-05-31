package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
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
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Astra product) {
        //noinspection EmptyTryBlock
        try (Astra astra = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("391705")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.expandMountPoint();
        }
    }

    @Disabled
    @TmsLink("391699")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.restart();
        }
    }

    @TmsLink("1685463")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Реинвентаризация ВМ (Linux) {0}")
    void updateVmInfo(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.updateVmInfo();
        }
    }

    @Disabled
    @TmsLink("391702")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.stopSoft();
            astra.start();
        }
    }

    @TmsLink("391704")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.resize(astra.getMaxFlavor());
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.stopHard();
            astra.start();
        }
    }

    @TmsLink("1090927")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка прав у ролей пользователя {0}")
    void checkCreate(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkUserGroupBySsh();
        }
    }

    @TmsLink("382910")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Проверка создания {0}")
    void checkCreateAd(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.checkCertsBySsh();
        }
    }

    @TmsLink("391698")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Astra product) {
        try (Astra astra = product.createObjectExclusiveAccess()) {
            astra.deleteObject();
        }
    }
}
