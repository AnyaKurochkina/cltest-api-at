package api.cloud.orderService;

import api.Tests;
import core.helper.StringUtils;
import core.utils.AssertUtils;
import core.utils.ssh.SshClient;
import io.qameta.allure.*;
import lombok.SneakyThrows;
import models.cloud.orderService.products.Artemis;
import models.cloud.subModels.Flavor;
import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@Epic("Продукты")
@Feature("Artemis Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("artemis_astra"), @Tag("prod")})
@DisabledIfEnv("ift")
@Execution(ExecutionMode.SAME_THREAD)
public class ArtemisAstraTest extends Tests {

    @Mock
    public static Artemis loadBalancer = Artemis.builder().platform("OpenStack").env("DEV").segment("dev-srv-app").build().buildFromLink("https://prod-portal-front.cloud.vtb.ru/all/orders/c1dfe43f-8f3c-4d54-b280-6981c1fca2dc/main?context=proj-ln4zg69jek&type=project&org=vtb");


    @TmsLink("982658")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Artemis product, Integer num) {
        //noinspection EmptyTryBlock
        try (Artemis artemis = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLinks({@TmsLink("982647"), @TmsLink("982652")})
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
            Flavor flavor = artemis.getMaxFlavor();
            artemis.verticalScaling(flavor);
            if (artemis.isDev()) {
                artemis.runOnAllNodesBySsh(client -> {
                    Integer memory = (Integer.parseInt(artemis.executeSsh(client, "free  | grep \"Mem\" | awk '{print $2}'")) / 1048576) + 1;
                    Integer cpu = Integer.parseInt(artemis.executeSsh(client, "cat /proc/cpuinfo | grep processor | wc -l"));
                    String memConfig = artemis.executeSsh(client, "sudo cat /app/etc/systemd/artemis.service.conf | grep \"Xmx\"");

                    Assertions.assertAll("Проверка изменений по SSH на vm " + client.getHost(), () -> Assertions.assertEquals(flavor.getMemory(), memory, "Размер ОЗУ не изменился"), () -> Assertions.assertEquals(flavor.getCpus(), cpu, "Размер CPU не изменился"), () -> Assertions.assertEquals(memConfig, "-Xmx" + flavor.getMemory() / 4 + "G", "Размер не mem / 4"));
                });
            }
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
    @TmsLinks({@TmsLink("982654"), @TmsLink("982656")})
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
            if (artemis.isDev()) {
                artemis.runOnAllNodesBySsh(client -> Assertions.assertAll("Проверка изменений по SSH на vm " + client.getHost(),
                        () -> assertCertificateNameMatches(artemis, client),
                        () -> assertCertificateStartDateMatches(artemis, client),
                        () -> assertCertificateEndDateMatches(artemis, client)));
            }
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

    @SneakyThrows
    void asserDate(String expectedDate, String actualDate, String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date expectedDateTime = formatter.parse(expectedDate);
        Date actualDateTime = formatter.parse(actualDate);
        AssertUtils.AssertDate(expectedDateTime, actualDateTime, 5, message);
    }

    @Step("[Проверка] Обновление имени сертификата")
    void assertCertificateNameMatches(Artemis artemis, SshClient client) {
        String cnOrderService = OrderServiceSteps.getObjectClass(artemis, "data.find{it.type=='cluster'}.data.config.cert_cn", String.class);
        String cn = artemis.executeSsh(client, StringUtils.format("sudo openssl x509 -noout -subject -in '/app/itc/certs/{}.corp.dev.vtb-vtb-artemis.pem' | awk --field-separator '=' '{print $(NF)}' | xargs", client.getHost()));
        Assertions.assertEquals(cnOrderService, cn, "Имя сертификата не совпадает");
    }

    @Step("[Проверка] Обновление даты начала сертификата")
    void assertCertificateStartDateMatches(Artemis artemis, SshClient client) {
        String startDateOrderService = OrderServiceSteps.getObjectClass(artemis, "data.find{it.type=='cluster'}.data.config.cert_start_date", String.class);
        String startDate = artemis.executeSsh(client, StringUtils.format("date --date=\"$(sudo openssl x509 -startdate -noout -in '/app/itc/certs/{}.corp.dev.vtb-vtb-artemis.pem' | cut -d= -f 2)\" -Is -u", client.getHost()));
        asserDate(startDateOrderService, startDate, "Дата начала сертификата не совпадает");
    }

    @Step("[Проверка] Обновление даты окончания сертификата")
    void assertCertificateEndDateMatches(Artemis artemis, SshClient client) {
        String endDateOrderService = OrderServiceSteps.getObjectClass(artemis, "data.find{it.type=='cluster'}.data.config.cert_end_date", String.class);
        String endDate = artemis.executeSsh(client, StringUtils.format("date --date=\"$(sudo openssl x509 -enddate -noout -in '/app/itc/certs/{}.corp.dev.vtb-vtb-artemis.pem' | cut -d= -f 2)\" -Is -u", client.getHost()));
        asserDate(endDateOrderService, endDate, "Дата окончания сертификата не совпадает");
    }
}
