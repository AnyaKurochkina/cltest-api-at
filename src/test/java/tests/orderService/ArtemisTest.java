package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Artemis;
import models.orderService.products.Artemis;
import models.orderService.products.WildFly;
import models.portalBack.AccessGroup;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Artemis")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("artemis"), @Tag("prod")})
public class ArtemisTest extends Tests {

    @TmsLink("854210")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Artemis product) {
        //noinspection EmptyTryBlock
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("854220")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать сервис {0}")
    void createService(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("randomserv1", "randomcert1");
        }
    }

    @TmsLink("854221")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить сервис {0}")
    void deleteService(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("randomserv2", "randomcert2");
            artemis.deleteService("randomserv2");
        }
    }

    @TmsLink("854222")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать клиента Own без сервиса {0}")
    void createClientOwn(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createClient("own", "randomusr1", "randomcertif1");
        }
    }

    @TmsLink("854224")
    @Tag("newTests")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать клиента temporary {0}")
    void createClientTemporary(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createClient("temporary", "randomusr2", "randomcertif2");
        }
    }

    @TmsLink("854225")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить клиента {0}")
    void deleteClient(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createClient("own", "randomusr3", "randomcertif3");
            artemis.deleteClient("randomusr3");
        }
    }

    @TmsLink("854223")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать клиента Own с сервисом {0}")
    void createClientOwnWithService(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("someserv", "somecertif");
            artemis.createClientWithService("own", "randomusr4", "randomcertif4", "someserv");
        }
    }


    @TmsLink("854218")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.expandMountPoint();
        }
    }

    @TmsLink("854214")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.restart();
        }
    }

    @TmsLink("854211")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopSoft();
            artemis.start();
        }
    }

    @TmsLink("854213")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopHard();
            try {
                artemis.resize(artemis.getMaxFlavor());
            } finally {
                artemis.start();
            }
        }
    }

    @TmsLink("854215")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopHard();
            artemis.start();
        }
    }

    @TmsLink("854212")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopHard();
            artemis.start();
        }
    }

    @TmsLink("854216")//833647
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.refreshVmConfig();
        }
    }

    @TmsLink("854219")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Artemis product) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.deleteObject();
        }
    }
}
