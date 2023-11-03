package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.PostgreSQL;
import models.cloud.subModels.Flavor;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.util.Arrays;
import java.util.List;

@Epic("Продукты")
@Feature("PostgreSQL Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgresql"), @Tag("prod")})
public class PostgreSQLTest extends Tests {
    static final String dbName = "cached_bd";

    @TmsLink("1057046")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(PostgreSQL product) {
        //noinspection EmptyTryBlock
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {}
    }

    @Disabled
    @TmsLink("1057048")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Расширить {0}")
    void expandMountPoint(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.expandMountPoint();
        }
    }

    @TmsLink("1057043")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить БД {0}")
    void createDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
        }
    }

    @TmsLink("1057047")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверка подключения к БД {0}")
    void checkDbConnection(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            String db = "bd_for_check_connection";
            postgreSQL.createDb(db);
            postgreSQL.checkConnection(db);
            postgreSQL.removeDb(db);
        }
    }

    @TmsLink("1057037")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить пользователя {0}")
    void createDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.createDbmsUser("chelik1", "user", dbName);
        }
    }

    @TmsLink("1057042")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Сбросить пароль {0}")
    void resetPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.createDbmsUser("chelikforreset1", "user", dbName);
            postgreSQL.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("1057039")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.resetDbOwnerPassword(dbName);
        }
    }

    @TmsLink("1057044")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить пользователя {0}")
    void removeDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.createDbmsUser("chelikforreset2", "user", dbName);
            postgreSQL.removeDbmsUser("chelikforreset2", dbName);
        }
    }

    @TmsLink("1701481")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить extensions {0}")
    void updateExtensions(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.updateExtensions(dbName, Arrays.asList("pg_trgm", "hstore"));
        }
    }

    @TmsLink("1701482")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Актуализировать extensions {0}")
    void getExtensions(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.getExtensions(dbName, "ltree");
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить точку монтирования /pg_audit {0}")
    void addMountPointPgAudit(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.addMountPointPgAudit();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить точку монтирования /pg_backup {0}")
    void addMountPointPgBackup(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.addMountPointPgBackup();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить точку монтирования /pg_walarchive {0}")
    void addMountPointPgWalarchive(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.addMountPointPgWalarchive();
        }
    }

    @TmsLink("1057040")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Максимизировать max_connections {0}")
    void updateMaxConnections(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
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

    @TmsLink("1701483")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Обновить минорную версию СУБД {0}")
    void updatePostgresql(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updatePostgresql();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Обновить ОС {0}")
    void updateOs(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue(postgreSQL.isDev(), "Тест включен только для dev среды");
            postgreSQL.updateOs();
        }
    }

    @TmsLink("1701485")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Получить актуальную конфигурацию {0}")
    void getConfiguration(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.getConfiguration();
        }
    }

    @Disabled
    @TmsLink("1057050")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Перезагрузить {0}")
    void restart(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.restart();
        }
    }

    @TmsLink("1057052")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить БД {0}")
    void removeDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.removeDb(dbName);
        }
    }

    @Disabled
    @TmsLink("1057038")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить {0}")
    void stopSoft(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.stopSoft();
            postgreSQL.start();
        }
    }

    @TmsLink("1057041")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить конфигурацию {0}")
    void resize(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            List<Flavor> list = ReferencesStep.getProductFlavorsLinkedListByFilter(postgreSQL);
            Assertions.assertTrue(list.size() > 1, "Кол-во flavors: " + list.size());
            postgreSQL.resize(list.get(1));
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("1057045"), @TmsLink("1057053")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить принудительно/Включить {0}")
    void stopHard(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.stopHard();
            postgreSQL.start();
        }
    }

    @TmsLink("1057049")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить default_transaction_isolation {0}")
    void updateDti(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateDti("REPEATABLE READ");
        }
    }

    @TmsLinks({@TmsLink("1116377"), @TmsLink("1116378"), @TmsLink("1104181")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Назначить/Убрать предел подключений {0}")
    void setConnLimit(PostgreSQL product) {
//        Assumptions.assumeTrue("LT".equalsIgnoreCase(product.getEnv()), "Тест включен только для среды LT");
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            postgreSQL.setConnLimit(dbName, 20);
            postgreSQL.removeConnLimit(dbName);
        }
    }

    @TmsLink("994970")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверка прав у ролей пользователя {0}")
    void checkUserPermissions(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb(dbName);
            String ip = (String) OrderServiceSteps.getProductsField(postgreSQL, "product_data.find{it.hostname.contains('-pgc')}.ip");
            postgreSQL.checkUseSsh(ip, dbName);
        }
    }

    @TmsLink("1057051")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.deleteObject();
        }
    }
}
