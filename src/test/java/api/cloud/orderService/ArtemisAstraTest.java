package api.cloud.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Artemis;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;

import java.util.Collections;

@Epic("Продукты")
@Feature("Artemis Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("artemis_astra"), @Tag("prod")})
@DisabledIfEnv("ift")
public class ArtemisAstraTest extends Tests {

    @TmsLink("982658")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Artemis product, Integer num) {
        //noinspection EmptyTryBlock
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLinks({@TmsLink("982647"),@TmsLink("982652")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать/удалить сервис {0}")
    void createService(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("randomserv2", "CN=randomcert2");
            artemis.deleteService("randomserv2");
        }
    }

    @TmsLink("982661")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать клиента Own без сервиса {0}")
    void createClientOwn(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            Artemis.Client client = new Artemis.Client();
            client.setClientTypes("own");
            client.setName("randomusr1");
            client.setOwnerCert("CN=randomcertif1");
            artemis.createClient(client);
        }
    }

    @TmsLink("982653")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать клиента temporary {0}")
    void createClientTemporary(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("someserv2", "CN=somecertif");
            Artemis.Client client = new Artemis.Client();
            client.setClientTypes("temporary");
            client.setName("randomusr2");
            client.setOwnerCert("CN=randomcertif2");
            client.setServiceNames(Collections.singletonList("someserv2"));
            artemis.createClient(client);
        }
    }

    @TmsLink("982655")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить клиента {0}")
    void deleteClient(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            Artemis.Client client = new Artemis.Client();
            client.setClientTypes("own");
            client.setName("randomusr3");
            client.setOwnerCert("CN=randomcertif3");
            artemis.createClient(client);
            artemis.deleteClient("randomusr3");
        }
    }

    @TmsLink("982651")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать клиента Own с сервисом {0}")
    void createClientOwnWithService(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.createService("someserv", "CN=somecertif");
            Artemis.Client client = new Artemis.Client();
            client.setClientTypes("own");
            client.setName("randomusr3");
            client.setOwnerCert("CN=randomcertif3");
            client.setServiceNames(Collections.singletonList("someserv"));
        }
    }

    @TmsLink("982662")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезапуск кластера {0}")
    void restart(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.restart();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Вертикальное масштабирование {0}")
    void verticalScaling(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.verticalScaling();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Включение/отключение протоколов {0}")
    void switchProtocol(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.switchProtocol(true, false);
        }
    }

    @Disabled
    @TmsLink("982648")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopSoft();
            artemis.start();
        }
    }

    @Disabled
    @TmsLink("982649")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
//            artemis.stopHard();
//            try {
                artemis.resize(artemis.getMaxFlavorLinuxVm());
//            } finally {
//                artemis.start();
//            }
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("982654"),@TmsLink("982656")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Включить/Выключить принудительно {0}")
    void stopHard(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.stopHard();
            artemis.start();
        }
    }

    @TmsLink("982650")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверить конфигурацию {0}")
    void refreshVmConfig(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.refreshVmConfig();
        }
    }

    @TmsLink("1092312")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Отправить конфигурацию кластера на email {0}")
    void exportConf(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.exportConf();
        }
    }

    @TmsLink("1093969")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновление сертификатов Artemis {0}")
    void updateCertsArtemis(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.updateCertsArtemis();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Аварийное обновление сертификатов {0}")
    void updateExpiredCertsArtemis(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.updateExpiredCertsArtemis();
        }
    }

    @TmsLink("982660")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(Artemis product, Integer num) {
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
            artemis.deleteObject();
        }
    }
}
