package api.cloud.orderService;

import api.Tests;
import core.helper.StringUtils;
import core.utils.AssertUtils;
import core.utils.Waiting;
import core.utils.ssh.SshClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import io.restassured.common.mapper.TypeRef;
import lombok.SneakyThrows;
import models.cloud.orderService.products.RedisSentinel;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.List;

@Epic("Продукты")
@Feature("Redis Sentinel")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("redis_sentinel"), @Tag("prod")})
public class RedisSentinelTest extends Tests {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(RedisSentinel product, Integer num) {
        //noinspection EmptyTryBlock
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Сбросить пароль {0}")
    void resetPassword(RedisSentinel product, Integer num) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.resetPassword();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать/Удалить пользователя {0}")
    void addUser(RedisSentinel product, Integer num) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.createUser("user2", "mzVaohLVnTnH2XrEEa9iLEVHWbN2XP");
            redis.deleteUser("user2");
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить ОС {0}")
    void updateOsStandalone(RedisSentinel product, Integer num) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(redis.isDev(), "Тест включен только для dev среды");
            redis.updateOsStandalone();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка нод мастера и репликации {0}")
    void checkReplication(RedisSentinel product, Integer num) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(redis.isDev(), "Тест включен только для dev среды");
            TypeRef<List<String>> typeReference = new TypeRef<List<String>>() {
            };
            List<String> hosts = OrderServiceSteps.getObjectClass(redis,
                    "data.findAll{it.type == 'vm' && it.data.config.node_roles.contains('redis')}.data.config.hostname", typeReference);

            checkSentinelConfig(redis, hosts);
            checkSwitchMaster(redis, hosts);
            checkReplication(redis, hosts);
        }
    }

    @SneakyThrows
    @Step("[Проверка] Репликация")
    private static void checkReplication(RedisSentinel redis, List<String> hosts) {
        SshClient master = findMasterNode(hosts, redis);
        SshClient replica = SshClient.builder().env(redis.envType())
                .host(hosts.stream().filter(e -> !e.equals(master.getHost())).findFirst().orElseThrow(Exception::new)).build();

        final String cmdSetValue = StringUtils.format("redis-cli --user {} --pass {} -h {} set testkey 123",
                redis.getAppUser(), redis.getAppUserPassword(), master.getHost());
        redis.executeSsh(master, cmdSetValue);

        final String cmdGetValue = StringUtils.format("redis-cli --user {} --pass {} -h {} get testkey",
                redis.getAppUser(), redis.getAppUserPassword(), replica.getHost());
        Assertions.assertTrue(redis.executeSsh(replica, cmdGetValue).endsWith("123"), "Неверный ответ от replica node");
    }

    @Step("[Проверка] Sentinel config")
    private static void checkSentinelConfig(RedisSentinel redis, List<String> hosts) {
        redis.runOnAllNodesBySsh(client -> AssertUtils.assertContains(redis.executeSsh(client, "sudo cat /etc/redis/sentinel.conf"),
                "sentinel known-replica", "sentinel known-sentinel"), hosts);
    }

    @Step("[Проверка] Переключение master node")
    private static void checkSwitchMaster(RedisSentinel redis, List<String> hosts) {
        SshClient master = findMasterNode(hosts, redis);
        redis.executeSsh(master, "sudo systemctl stop redis-server.service");
        Waiting.sleep(5000);
        redis.executeSsh(master, "sudo systemctl start redis-server.service");
        Waiting.sleep(20000);
        Assertions.assertFalse(isMasterNode(master.getHost(), redis), "Node Осталась master");
        long countMaster = hosts.stream().filter(e -> !e.equals(master.getHost())).filter(host -> isMasterNode(host, redis)).count();
        Assertions.assertEquals(1, countMaster, "Не найдена новая master node");
    }

    @Step("[Проверка] Является ли {host} master node")
    private static boolean isMasterNode(String host, RedisSentinel redis) {
        SshClient client = SshClient.builder().host(host).env(redis.envType()).build();
        return !redis.executeSsh(client, "sudo cat /etc/redis/redis.conf").contains("replicaof");
    }

    @Step("Поиск master node host")
    private static SshClient findMasterNode(List<String> hosts, RedisSentinel redis) {
        return redis.findNodeBySsh(client -> isMasterNode(client.getHost(), redis), hosts);
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка создания {0}")
    void checkConnect(RedisSentinel product, Integer num) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.checkConnect();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить параметр notify-keyspace-events {0}")
    void changeNotifyKeyspaceEvents(RedisSentinel product, Integer num) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.changeNotifyKeyspaceEvents("KEA");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(RedisSentinel product, Integer num) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.deleteObject();
        }
    }
}
