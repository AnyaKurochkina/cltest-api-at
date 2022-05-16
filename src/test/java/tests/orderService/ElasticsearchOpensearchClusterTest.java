package tests.orderService;

import io.qameta.allure.TmsLink;
import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.ElasticsearchOpensearchCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Elasticsearch Opensearch Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("elasticsearch_opensearch_cluster"), @Tag("prod")})
public class ElasticsearchOpensearchClusterTest extends Tests {

    final String productName = "Elasticsearch Opensearch cluster";

    @TmsLink("671243")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("671245")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.refreshVmConfig();
        }
    }

    @TmsLink("671250")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();

        }
    }

    @TmsLink("671252")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.stopSoft();
            elastic.start();
        }
    }

    @TmsLink("671253")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.stopHard();
            elastic.start();
        }
    }

    @TmsLink("671254")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить по питанию {0}")
    void restart(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.restart();
        }
    }

    @TmsLink("671257")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ElasticsearchOpensearchCluster product) {
        product.setProductName(productName);
        try (ElasticsearchOpensearchCluster elastic = product.createObjectExclusiveAccess()) {
            elastic.deleteObject();
        }
    }
}
