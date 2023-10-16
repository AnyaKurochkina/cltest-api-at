package api.cloud.orderService;

import api.Tests;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;


@Epic("Продукты")
@Feature("Apache Airflow")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("airflow"), @Tag("prod")})
public class ApacheAirflowTest extends Tests {

    private static void createPostgres(ApacheAirflow product) {
        AbstractPostgreSQL postgreSQL;

        AbstractPostgreSQL abstractPostgreSQL = PostgreSQL.builder().env(product.getEnv()).build();
        if ("LT".equalsIgnoreCase(product.getEnv()) || product.isProd())
            abstractPostgreSQL = PostgresSQLCluster.builder().env(product.getEnv()).build();

        postgreSQL = abstractPostgreSQL.createObjectExclusiveAccess();
        String dbName = "airflow";
        postgreSQL.createDb(dbName);
        product.setPgAdminPassword(postgreSQL.getAdminPassword());
        product.setDbServer((String) OrderServiceSteps.getProductsField(postgreSQL, "product_data.find{it.hostname.contains('-pgc')}.ip"));
        product.setDbUser(new DbUser(dbName, dbName + "_admin"));
        postgreSQL.close();
    }

    @TmsLink("1421430")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(ApacheAirflow product, PostgreSQL ignore) {
        createPostgres(product);
        //noinspection EmptyTryBlock
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("1421459")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Расширить {0}")
    void expand(ApacheAirflow product, PostgreSQL ignore) {
        createPostgres(product);
        try (ApacheAirflow apacheAirflow = product.createObjectExclusiveAccess()) {
            apacheAirflow.expandMountPoint();
        }
    }

    @TmsLink("1421448")
    @MarkDelete
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    void delete(ApacheAirflow product, PostgreSQL ignore) {
        createPostgres(product);
        try (ApacheAirflow airflow = product.createObjectExclusiveAccess()) {
            airflow.deleteObject();
        }
    }

}
