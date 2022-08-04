package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.Artemis;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Artemis Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("artemis_astra"), @Tag("prod")})
public class ArtemisAstraTest extends Tests {
    final static String productName = "VTB Apache ActiveMQ Artemis Astra";

    @TmsLink("982658")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Artemis product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLinks({@TmsLink("982647"),@TmsLink("982652")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать/удалить сервис {0}")
    void createService(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("randomserv2", "randomcert2");
            artemis.deleteService("randomserv2");
        }
    }

    @TmsLink("982661")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать клиента Own без сервиса {0}")
    void createClientOwn(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createClient("own", "randomusr1", "randomcertif1");
        }
    }

    @TmsLink("982653")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать клиента temporary {0}")
    void createClientTemporary(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createClient("temporary", "randomusr2", "randomcertif2");
        }
    }

    @TmsLink("982655")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить клиента {0}")
    void deleteClient(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createClient("own", "randomusr3", "randomcertif3");
            artemis.deleteClient("randomusr3");
        }
    }

    @TmsLink("982651")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать клиента Own с сервисом {0}")
    void createClientOwnWithService(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("someserv", "somecertif");
            artemis.createClientWithService("own", "randomusr4", "randomcertif4", "someserv");
        }
    }


    @TmsLink("982657")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.expandMountPoint();
        }
    }

    @TmsLink("982662")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.restart();
        }
    }

    @TmsLink("982648")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopSoft();
            artemis.start();
        }
    }

    @TmsLink("982649")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopHard();
            try {
                artemis.resize(artemis.getMaxFlavor());
            } finally {
                artemis.start();
            }
        }
    }

    @TmsLinks({@TmsLink("982654"),@TmsLink("982656")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить/Выключить принудительно {0}")
    void stopHard(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopHard();
            artemis.start();
        }
    }

    @TmsLink("982650")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.refreshVmConfig();
        }
    }

    @TmsLink("1092312")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Отправить конфигурацию кластера на email {0}")
    void exportConf(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.exportConf();
        }
    }

    @TmsLink("982660")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Artemis product) {
        product.setProductName(productName);
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.deleteObject();
        }
    }
}
