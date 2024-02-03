package api.cloud.orderService;

import api.Tests;
import core.utils.ssh.SshClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.PostgresSQLCluster;
import org.junit.EnabledIfEnv;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("PostgresSQL Cluster Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgres_sql_cluster_astra"), @Tag("prod")})
public class PostgresSQLClusterAstraTest extends Tests {
    static final String dbName = "db_name";

    @TmsLink("810039")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(PostgresSQLCluster product, Integer num) {
        //noinspection EmptyTryBlock
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @EnabledIfEnv({"prod", "blue"})
    @ParameterizedTest(name = "[{1}] Заказ на быстрых дисках {0}")
    void checkDiskVm(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            List<String> envs = Arrays.asList("LT", "PROD");
            Assumptions.assumeTrue(envs.contains(product.getEnv()), "Тест только для сред " + Arrays.toString(envs.toArray()));
            String type = (postgres.getEnv().equals("PROD")) ? "nvme" : "ssd";
            postgres.checkVmDisk(new HashMap<String, String>() {{
                put("postgresql", type);
                put("etcd", "hdd");
            }});
        }
    }

    @TmsLink("810032")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить /pg_data {0}")
    void expandMountPoint(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.expandMountPoint();
        }
    }

    @TmsLink("810040")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить БД {0}")
    void createDb(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
        }
    }

    @TmsLink("810045")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверить подключение к БД PostgresSQLCluster {0}")
    void checkBdConnection(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.checkConnection(dbName);
        }
    }

    @TmsLink("810041")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить пользователя {0}")
    void createDbmsUser(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("testchelik1", "user", dbName);
        }
    }

    @TmsLink("810034")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Сбросить пароль пользователя {0}")
    void resetPassword(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("chelikforreset1", "user", dbName);
            postgres.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("810042")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить пользователя {0}")
    void removeDbmsUser(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("chelikforremove2", "user", dbName);
            postgres.removeDbmsUser("chelikforremove2", dbName);
//            postgres.removeDb("cached_bd");
        }
    }

    @TmsLink("810044")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.resetDbOwnerPassword(dbName);
        }
    }

    @TmsLink("810043")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить БД {0}")
    void removeDb(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.removeDb(dbName);
        }
    }

    @Disabled
    @TmsLink("810035")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.restart();
        }
    }

    @TmsLink("1256575")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию нод СУБД {0}")
    void resize(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.resize(postgres.getMaxFlavor());
        }
    }

    @Disabled
    @TmsLink("810038")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopSoft();
            postgres.start();
        }
    }

    @TmsLink("1117590")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Назначить предел подключений {0}")
    void setConnLimit(PostgresSQLCluster product, Integer num) {
//        Assumptions.assumeTrue("LT".equalsIgnoreCase(product.getEnv()), "Тест включен только для среды LT");
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.setConnLimit(dbName, 30);
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("810036"),@TmsLink("810037")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopHard();
            postgres.start();
        }
    }

    @TmsLink("851767")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка прав у ролей пользователя {0}")
    void checkCreate(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            String ip = (String) OrderServiceSteps.getProductsField(postgres, "product_data.find{it.hostname.contains('-pgc')}.ip");
            postgres.checkUseSsh(ip, dbName);
        }
    }

    @TmsLink("851804")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Просмотр активного хоста {0}")
    void checkActiveHost(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            String ip = (String) OrderServiceSteps.getProductsField(postgres, "product_data.find{it.hostname.contains('-pgc')}.ip");
            assertContains(postgres.executeSsh(SshClient.builder().host(ip).env(postgres.envType()).build(),
                    "sudo patronictl -c /etc/patroni/patroni.yml list"), "Leader");
        }
    }

    @TmsLink("1701474")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить extensions {0}")
    void updateExtensions(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.updateExtensions(dbName, Arrays.asList("pg_trgm", "hstore"));
        }
    }

    @TmsLink("1701475")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Актуализировать extensions {0}")
    void getExtensions(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.getExtensions(dbName, "ltree");
        }
    }

    @TmsLink("1701476")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Получить актуальную конфигурацию {0}")
    void getConfiguration(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.getConfiguration();
        }
    }

    @TmsLink("1701477")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Максимизировать max_connections {0}")
    void updateMaxConnections(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            if(postgres.isDev()) {
                postgres.updateMaxConnectionsBySsh(500);
                postgres.updateMaxConnections();
                Assertions.assertEquals(500, Integer.valueOf(postgres.getCurrentMaxConnections()));

                postgres.updateMaxConnectionsBySsh(99);
                postgres.updateMaxConnections();
                Assertions.assertEquals(postgres.maxConnections(), Integer.valueOf(postgres.getCurrentMaxConnections()));
            }
            postgres.updateMaxConnections();
            Assertions.assertEquals(postgres.maxConnections(), Integer.valueOf(postgres.getCurrentMaxConnections()));
        }
    }

    @TmsLink("1701478")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить default_transaction_isolation {0}")
    void updateDti(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.updateDti("REPEATABLE READ");
        }
    }

    @TmsLink("1701479")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить минорную версию СУБД {0}")
    void updatePostgresql(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.updatePostgresql();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Актуализировать версию СУБД {0}")
    void updateVersionDb(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.updateVersionDb();
        }
    }

    @TmsLink("1701480")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить точку монтирования /pg_walarchive {0}")
    void addMountPointPgWalarchive(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            List<String> envs = Arrays.asList("LT", "PROD");
            Assumptions.assumeFalse(envs.contains(product.getEnv()), "Тест выключен для сред " + Arrays.toString(envs.toArray()));
            postgres.addMountPointPgWalarchive();
        }
    }

    @TmsLink("1762836")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Настроить кластер для интеграции с Debezium {0}")
    void configureDebezium(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.configureDebezium();
        }
    }

    @TmsLink("1762837")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Настроить БД для интеграции с Debezium {0}")
    void configureDebeziumDb(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.configureDebeziumDb();
        }
    }

    @TmsLink("1762839")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать/удалить логический слот {0}")
    void createLogicalSlot(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createLogicalSlot("slot_name");
            postgres.removeLogicalSlot("slot_name");
        }
    }

    @TmsLink("1762840")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать/удалить публикацию {0}")
    void createPublication(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createPublication("pub_dbzm");
            postgres.removePublication("pub_dbzm");
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить точку монтирования /pg_audit {0}")
    void addMountPointPgAudit(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.addMountPointPgAudit();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить точку монтирования /pg_backup {0}")
    void addMountPointPgBackup(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.addMountPointPgBackup();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить на хост etcd точку монтирования /app/backup (5gb) {0}")
    void etcdAddMountPointAppBackup(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.etcdAddMountPointAppBackup();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить на хост etcd точку монтирования /app/logs (10gb) {0}")
    void addMountPointAppLogs(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.addMountPointAppLogs();
        }
    }

    @TmsLink("810033")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(PostgresSQLCluster product, Integer num) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.deleteObject();
        }
    }
}
