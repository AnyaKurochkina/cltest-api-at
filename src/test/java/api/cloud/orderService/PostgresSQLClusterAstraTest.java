package api.cloud.orderService;

import api.Tests;
import core.utils.ssh.SshClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.PostgresSQLCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.Arrays;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("PostgresSQL Cluster Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgresSqlClusterAstra"), @Tag("prod")})
public class PostgresSQLClusterAstraTest extends Tests {
    public static final String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";
    static final String dbName = "db_name";

    @TmsLink("810039")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgresSQLCluster product) {
        //noinspection EmptyTryBlock
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("810032")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить /pg_data {0}")
    void expandMountPoint(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.expandMountPoint();
        }
    }

    @TmsLink("810040")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
        }
    }

    @TmsLink("810045")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить подключение к БД PostgresSQLCluster {0}")
    void checkBdConnection(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            postgres.checkConnection(dbName);
        }
    }

    @TmsLink("810041")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            postgres.createDbmsUser("testchelik1", "user", dbName);
        }
    }

    @TmsLink("810034")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            postgres.createDbmsUser("chelikforreset1", "user", dbName);
            postgres.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("810042")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            postgres.createDbmsUser("chelikforremove2", "user", dbName);
            postgres.removeDbmsUser("chelikforremove2", dbName);
//            postgres.removeDb("cached_bd");
        }
    }

    @TmsLink("810044")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            postgres.resetDbOwnerPassword(dbName);
        }
    }

    @TmsLink("810043")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            postgres.removeDb(dbName);
        }
    }

    @Disabled
    @TmsLink("810035")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.restart();
        }
    }

    @TmsLink("1256575")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию нод СУБД {0}")
    void resize(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.resize(postgres.getMaxFlavor());
        }
    }

    @Disabled
    @TmsLink("810038")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopSoft();
            postgres.start();
        }
    }

    @TmsLink("1117590")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Назначить предел подключений {0}")
    void setConnLimit(PostgresSQLCluster product) {
//        Assumptions.assumeTrue("LT".equalsIgnoreCase(product.getEnv()), "Тест включен только для среды LT");
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            postgres.setConnLimit(dbName, 30);
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("810036"),@TmsLink("810037")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopHard();
            postgres.start();
        }
    }

    @TmsLink("851767")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Проверка прав у ролей пользователя {0}")
    void checkCreate(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName, adminPassword);
            String ip = (String) OrderServiceSteps.getProductsField(postgres, "product_data.find{it.hostname.contains('-pgc')}.ip");
            postgres.checkUseSsh(ip, dbName);
        }
    }

    @TmsLink("851804")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Просмотр активного хоста {0}")
    void checkActiveHost(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            String ip = (String) OrderServiceSteps.getProductsField(postgres, "product_data.find{it.hostname.contains('-pgc')}.ip");
            assertContains(postgres.executeSsh(new SshClient(ip, postgres.envType()),
                    "sudo patronictl -c /etc/patroni/patroni.yml list"), "Leader");
        }
    }

    @TmsLink("1701474")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить extensions {0}")
    void updateExtensions(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName, adminPassword);
            postgreSQL.updateExtensions(dbName, Arrays.asList("pg_trgm", "hstore"));
        }
    }

    @TmsLink("1701475")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Актуализировать extensions {0}")
    void getExtensions(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName, adminPassword);
            postgreSQL.getExtensions(dbName, "ltree");
        }
    }

    @TmsLink("1701476")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Получить актуальную конфигурацию {0}")
    void getConfiguration(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.getConfiguration();
        }
    }

    @TmsLink("1701477")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Максимизировать max_connections {0}")
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
    @ParameterizedTest(name = "Изменить default_transaction_isolation {0}")
    void updateDti(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateDti("REPEATABLE READ");
        }
    }

    @TmsLink("1701479")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить минорную версию СУБД {0}")
    void updatePostgresql(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updatePostgresql();
        }
    }

    @TmsLink("1701480")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить точку монтирования /pg_walarchive {0}")
    void addMountPointPgWalarchive(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.addMountPointPgWalarchive();
        }
    }

    @TmsLink("1762836")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Настроить кластер для интеграции с Debezium {0}")
    void configureDebezium(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.configureDebezium();
        }
    }

    @TmsLink("1762837")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Настроить БД для интеграции с Debezium {0}")
    void configureDebeziumDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName, adminPassword);
            postgreSQL.configureDebeziumDb();
        }
    }

    @TmsLink("1762839")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать/удалить логический слот {0}")
    void createLogicalSlot(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName, adminPassword);
            postgreSQL.createLogicalSlot("slot_name");
            postgreSQL.removeLogicalSlot("slot_name");
        }
    }

    @TmsLink("1762840")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать/удалить публикацию {0}")
    void createPublication(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName, adminPassword);
            postgreSQL.createPublication("pub_dbzm");
            postgreSQL.removePublication("pub_dbzm");
        }
    }

    @TmsLink("810033")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.deleteObject();
        }
    }
}
