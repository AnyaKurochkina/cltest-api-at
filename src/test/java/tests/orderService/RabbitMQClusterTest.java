package tests.orderService;

import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.RabbitMQCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epic("Продукты")
@Feature("RabbitMQCluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("rabbitmqcluster"), @Tag("prod")})
public class RabbitMQClusterTest extends Tests {

    @TmsLink("377645")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(RabbitMQCluster product) {
        //noinspection EmptyTryBlock
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("377638")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.expandMountPoint();
        }
    }

    @TmsLink("653492")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.refreshVmConfig();
        }
    }

    @TmsLink("377641")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.restart();
        }
    }

    @TmsLink("377644")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.stopSoft();
            rabbit.start();
        }
    }

    @TmsLink("377656")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать пользователя RabbitMQ {0}")
    void createUser(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.rabbitmqCreateUser("testapiuser");
        }
    }

    @TmsLink("377643")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.stopHard();
            rabbit.start();
        }
    }

    @TmsLink("377646")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(RabbitMQCluster product) {
        try (RabbitMQCluster rabbitMQCluster = product.createObjectExclusiveAccess()) {
            rabbitMQCluster.updateCerts();
        }
    }

    @TmsLink("707972")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание vhosts {0}")
    void addVhost(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.addVhost(Stream.generate(new Generex("[a-zA-Z0-9]{2,16}")::random)
                    .limit(new Random().nextInt(14) + 1).distinct().collect(Collectors.toList()));
        }
    }

    @TmsLink("707975")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление vhosts {0}")
    void deleteVhostAccessVhost(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            List<String> vhosts = Stream.generate(new Generex("[a-zA-Z0-9]{2,16}")::random)
                    .limit(new Random().nextInt(14) + 1).distinct().collect(Collectors.toList());
            rabbit.addVhost(vhosts);
            rabbit.deleteVhost(vhosts);
        }
    }

    @TmsLink("707976")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавление прав на vhost {0}")
    void addVhostAccess(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.rabbitmqCreateUser("vhostUser");
            rabbit.addVhost(Collections.singletonList("vhostAccess"));
            rabbit.addVhostAccess("vhostUser", Arrays.asList("READ", "WRITE", "CONFIGURE"), "vhostAccess");
        }
    }

    @TmsLink("707978")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление прав на vhost {0}")
    void deleteVhostAccess(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.rabbitmqCreateUser("vhostUserDelete");
            rabbit.addVhost(Collections.singletonList("accessDelete"));
            rabbit.addVhostAccess("vhostUserDelete", Arrays.asList("READ", "WRITE", "CONFIGURE"), "accessDelete");
            rabbit.deleteVhostAccess("vhostUserDelete", "accessDelete");
        }
    }

    @TmsLink("377642")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.stopHard();
            rabbit.start();
        }
    }

    @TmsLink("377639")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(RabbitMQCluster product) {
        try (RabbitMQCluster rabbit = product.createObjectExclusiveAccess()) {
            rabbit.deleteObject();
        }
    }
}
