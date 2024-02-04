package api.cloud.orderService;

import api.Tests;
import core.utils.ssh.SshClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.AbstractPostgreSQL;
import models.cloud.orderService.products.ApacheAirflow;
import models.cloud.orderService.products.PostgreSQL;
import models.cloud.orderService.products.PostgresSQLCluster;
import models.cloud.subModels.DbUser;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.Arrays;

import static core.utils.AssertUtils.assertContains;


@Epic("Продукты")
@Feature("Apache Airflow")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("airflow"), @Tag("prod")})
public class ApacheAirflowTest extends Tests {

    private static void createPostgres(ApacheAirflow product) {
        AbstractPostgreSQL abstractPostgreSQL = PostgreSQL.builder().env(product.getEnv()).build();
        abstractPostgreSQL.setSkip(product.isSkip());
        if (!product.isSkip()) {
            if ("LT".equalsIgnoreCase(product.getEnv()) || product.isProd())
                abstractPostgreSQL = PostgresSQLCluster.builder().env(product.getEnv()).build();
        }
        try (AbstractPostgreSQL postgreSQL = abstractPostgreSQL.createObjectExclusiveAccess()) {
            if (postgreSQL.deletedEntity())
                return;
            String dbName = "airflow";
            postgreSQL.createDb(dbName);
            product.setPgAdminPassword(postgreSQL.getAdminPassword());
            product.setDbServer(postgreSQL.pgcHost());
            product.setDbUser(new DbUser(dbName, dbName + "_admin"));
        }
    }

    @TmsLink("1421430")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Создать {0}")
    void create(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        //noinspection EmptyTryBlock
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("1421459")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Расширить {0}")
    void expand(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
            apacheAirflow.expandMountPoint();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Обновить сертификаты {0}")
    void updateCerts(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
            apacheAirflow.updateCerts();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Изменить группы добавления DAG-файлов {0}")
    void updateGroupAddDagFiles(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
            String accessGroupTechNew = apacheAirflow.accessGroup("service-accounts", "AT airflow new group");
            apacheAirflow.updateGroupAddDagFiles(accessGroupTechNew);
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Установить Cloudera CDH {0}")
    void airflowInstallExtras(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
            apacheAirflow.airflowInstallExtras();
            if (apacheAirflow.isDev()) {
                String ip = (String) OrderServiceSteps.getProductsField(apacheAirflow, "product_data[0].ip");
                assertContains(apacheAirflow.executeSsh(SshClient.builder().host(ip).env(apacheAirflow.envType()).build(),
                        "ls /app"), "cloudera", "extras");
            }
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Изменить группы доступа к консоли управления {0}")
    void airflowChangeWebAccess(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
            apacheAirflow.airflowChangeWebAccess(Arrays.asList(apacheAirflow.additionalAccessGroup(), apacheAirflow.accessGroup()));
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Обновить ОС {0}")
    void updateOs(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
            Assumptions.assumeFalse(product.isProd(), "Тест отключен для PROD среды");
            apacheAirflow.updateOs();
        }
    }

    @TmsLink("1421448")
    @MarkDelete
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{2}] Удалить {0}")
    void delete(ApacheAirflow product, AbstractPostgreSQL ignore, Integer num) {
        createPostgres(product);
        try (ApacheAirflow airflow = product.createObjectExclusiveAccess()) {
            airflow.deleteObject();
        }
    }
}
