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
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(PostgresSQLCluster product) {
        //noinspection EmptyTryBlock
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @EnabledIfEnv({"prod", "blue"})
    @ParameterizedTest(name = "[{index}] Заказ на быстрых дисках {0}")
    void checkDiskVm(PostgresSQLCluster product) {
        List<String> envs = Arrays.asList("LT", "PROD");
        Assumptions.assumeTrue(envs.contains(product.getEnv()), "Тест только для сред " + Arrays.toString(envs.toArray()));
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
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
    @ParameterizedTest(name = "[{index}] Расширить /pg_data {0}")
    void expandMountPoint(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.expandMountPoint();
        }
    }

    @TmsLink("810040")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить БД {0}")
    void createDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
        }
    }

    @TmsLink("810045")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверить подключение к БД PostgresSQLCluster {0}")
    void checkBdConnection(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.checkConnection(dbName);
        }
    }

    @TmsLink("810041")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить пользователя {0}")
    void createDbmsUser(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("testchelik1", "user", dbName);
        }
    }

    @TmsLink("810034")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Сбросить пароль пользователя {0}")
    void resetPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("chelikforreset1", "user", dbName);
            postgres.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("810042")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить пользователя {0}")
    void removeDbmsUser(PostgresSQLCluster product) {
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
    @ParameterizedTest(name = "[{index}] Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.resetDbOwnerPassword(dbName);
        }
    }

    @TmsLink("810043")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить БД {0}")
    void removeDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.removeDb(dbName);
        }
    }

    @Disabled
    @TmsLink("810035")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Перезагрузить {0}")
    void restart(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.restart();
        }
    }

    @TmsLink("1256575")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить конфигурацию нод СУБД {0}")
    void resize(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.resize(postgres.getMaxFlavor());
        }
    }

    @Disabled
    @TmsLink("810038")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить {0}")
    void stopSoft(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopSoft();
            postgres.start();
        }
    }

    @TmsLink("1117590")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Назначить предел подключений {0}")
    void setConnLimit(PostgresSQLCluster product) {
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
    @ParameterizedTest(name = "[{index}] Выключить принудительно/Включить {0}")
    void stopHard(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopHard();
            postgres.start();
        }
    }

    @TmsLink("851767")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] AD Проверка прав у ролей пользователя {0}")
    void checkCreate(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            String ip = (String) OrderServiceSteps.getProductsField(postgres, "product_data.find{it.hostname.contains('-pgc')}.ip");
            postgres.checkUseSsh(ip, dbName);
        }
    }

    @TmsLink("851804")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] AD Просмотр активного хоста {0}")
    void checkActiveHost(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            String ip = (String) OrderServiceSteps.getProductsField(postgres, "product_data.find{it.hostname.contains('-pgc')}.ip");
            assertContains(postgres.executeSsh(SshClient.builder().host(ip).env(postgres.envType()).build(),
                    "sudo patronictl -c /etc/patroni/patroni.yml list"), "Leader");
        }
    }

    @TmsLink("1701474")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить extensions {0}")
    void updateExtensions(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.updateExtensions(dbName, Arrays.asList("pg_trgm", "hstore"));
        }
    }

    @TmsLink("1701475")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Актуализировать extensions {0}")
    void getExtensions(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.getExtensions(dbName, "ltree");
        }
    }

    @TmsLink("1701476")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Получить актуальную конфигурацию {0}")
    void getConfiguration(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.getConfiguration();
        }
    }

    @TmsLink("1701477")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Максимизировать max_connections {0}")
    void updateMaxConnections(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            if(postgreSQL.isDev()) {
                postgreSQL.updateMaxConnectionsBySsh(500);
                postgreSQL.updateMaxConnections();
                Assertions.assertEquals(500, Integer.valueOf(postgreSQL.getCurrentMaxConnections()));

                postgreSQL.updateMaxConnectionsBySsh(99);
                postgreSQL.updateMaxConnections();
                Assertions.assertEquals(postgreSQL.maxConnections(), Integer.valueOf(postgreSQL.getCurrentMaxConnections()));
            }
            postgreSQL.updateMaxConnections();
            Assertions.assertEquals(postgreSQL.maxConnections(), Integer.valueOf(postgreSQL.getCurrentMaxConnections()));
        }
    }

    @TmsLink("1701478")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить default_transaction_isolation {0}")
    void updateDti(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateDti("REPEATABLE READ");
        }
    }

    @TmsLink("1701479")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Обновить минорную версию СУБД {0}")
    void updatePostgresql(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updatePostgresql();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Актуализировать версию СУБД {0}")
    void updateVersionDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateVersionDb();
        }
    }

    @TmsLink("1701480")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить точку монтирования /pg_walarchive {0}")
    void addMountPointPgWalarchive(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.addMountPointPgWalarchive();
        }
    }

    @TmsLink("1762836")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Настроить кластер для интеграции с Debezium {0}")
    void configureDebezium(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.configureDebezium();
        }
    }

    @TmsLink("1762837")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Настроить БД для интеграции с Debezium {0}")
    void configureDebeziumDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.configureDebeziumDb();
        }
    }

    @TmsLink("1762839")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать/удалить логический слот {0}")
    void createLogicalSlot(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.createLogicalSlot("slot_name");
            postgreSQL.removeLogicalSlot("slot_name");
        }
    }

    @TmsLink("1762840")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать/удалить публикацию {0}")
    void createPublication(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.createPublication("pub_dbzm");
            postgreSQL.removePublication("pub_dbzm");
        }
    }

    @TmsLink("810033")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.deleteObject();
        }
    }
}
