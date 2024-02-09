package api.cloud.orderService;

import api.Tests;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.RabbitMQClusterAstra;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("RabbitMQCluster")
@DisabledIfEnv("ift")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("rabbitmqcluster"), @Tag("prod")})
public class RabbitMQClusterTest extends Tests {
    private final static String ADP = RandomStringUtils.randomNumeric(5).toLowerCase();
    private final static String RIS_CODE = RandomStringUtils.randomNumeric(5).toLowerCase();

    @TmsLink("377645")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(RabbitMQClusterAstra product, Integer num) {
        //noinspection EmptyTryBlock
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
        }
    }

    @Disabled
    @TmsLink("377638")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.expandMountPoint();
        }
    }

    @TmsLink("653492")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверить конфигурацию {0}")
    void refreshVmConfig(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.refreshVmConfig();
        }
    }

    @Disabled
    @TmsLink("377641")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.restart();
        }
    }

    @Disabled
    @TmsLink("377644")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.stopSoft();
            rabbit.start();
        }
    }

    @TmsLink("377656")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать/Удалить пользователя RabbitMQ {0}")
    void createUser(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.rabbitmqCreateUser(ADP,RIS_CODE, "testapiuser");
            rabbit.rabbitmqDeleteUser("testapiuser");
        }
    }

    @TmsLinks({@TmsLink("377646"), @TmsLink("SOUL-6730")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить сертификаты. Проверка создание бэкапа сертификатов {0}")
    void updateCerts(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbitMQCluster = product.createObjectExclusiveAccess()) {
            Assertions.assertEquals("", rabbitMQCluster.executeSsh("ls /app/tls/backup/"));
            rabbitMQCluster.updateCerts();
            assertContains(rabbitMQCluster.executeSsh("ls /app/tls/backup/"), ".tar.gz");
        }
    }

    @TmsLinks({@TmsLink("707975"), @TmsLink("707972")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создание/Удаление vhosts {0}")
    void deleteVhostAccessVhost(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            List<String> vhosts = Stream.generate(new Generex("[a-zA-Z0-9]{2,16}")::random)
                    .limit(new Random().nextInt(14) + 1).distinct().collect(Collectors.toList());
            rabbit.addVhost(vhosts);
            rabbit.deleteVhost(vhosts);
        }
    }

    @TmsLinks({@TmsLink("707976"), @TmsLink("707978")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Редактирование/Удаление прав на vhost {0}")
    void addVhostAccess(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.rabbitmqCreateUser(ADP,RIS_CODE,"vhostUser");
            rabbit.addVhost(Collections.singletonList("vhostAccess"));
            rabbit.editVhostsAccess(RabbitMQClusterAstra.Permission.builder().vhostWrite("vhostAccess").build());
            rabbit.editVhostAccess("vhostUser", Arrays.asList("READ", "WRITE", "CONFIGURE"), "vhostAccess");
            rabbit.deleteVhostAccess("vhostUser", "vhostAccess");
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("377642"), @TmsLink("377643")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.stopHard();
            rabbit.start();
        }
    }

    @TmsLink("1060328")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания {0}")
    void checkCreate(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.rabbitmqCreateUser(ADP, RIS_CODE, "sshUser");
            assertContains(rabbit.executeSsh("sudo rabbitmqctl list_users"), "sshUser");
            rabbit.addVhost(Collections.singletonList("sshVhostAccess"));
            assertContains(rabbit.executeSsh("sudo rabbitmqctl list_vhosts"), "sshVhostAccess");
            rabbit.editVhostAccess("sshUser", Collections.singletonList("READ"), "sshVhostAccess");
            assertContains(rabbit.executeSsh("sudo rabbitmqctl list_permissions"), "sshUser");
        }
    }

    @TmsLink("SOUL-6729")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка дублирования промежуточных сертификатов RabbitMQ {0}")
    void checkDuplicateCerts(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            String[] certs = rabbit.executeSsh("cat /app/tls/certs/root.pem").split("-----END CERTIFICATE-----\n");
            Assertions.assertEquals(certs.length, Arrays.stream(certs).distinct().count());
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Произвести балансировку очередей {0}")
    void queueRebalancing(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.queueRebalancing();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Синхронизировать данные кластера {0}")
    void dataSynchronization(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.dataSynchronization();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить операционную систему {0}")
    void updateOs(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            Assumptions.assumeFalse(product.isProd(), "Тест отключен для PROD среды");
            rabbit.updateOs();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Вертикальное масштабирование {0}")
    void verticalScaling(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.verticalScaling();
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Редактировать группу доступа {0}")
    void accessGroupsOnTheWeb(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            String group = rabbit.accessGroup();
            rabbit.editAccessGroupsOnTheWeb(group, rabbit.getRole());
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(RabbitMQClusterAstra product, Integer num) {
        try (RabbitMQClusterAstra rabbit = product.createObjectExclusiveAccess()) {
            rabbit.deleteObject();
        }
    }
}
